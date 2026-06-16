package com.busanit401.spring_back.domain.service.rag;

import com.busanit401.spring_back.domain.repository.ChatBotRepository;
import com.busanit401.spring_back.dto.ChatBotFaqDocDTO;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.ko.KoreanAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.search.suggest.InputIterator;
import org.apache.lucene.search.suggest.Lookup.LookupResult;
import org.apache.lucene.search.suggest.analyzing.AnalyzingSuggester;
import org.apache.lucene.search.suggest.analyzing.FuzzySuggester;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.util.BytesRef;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.*;

/**
 * 추천검색어 자동완성기 (오타 보정 포함). <b>임베딩·LLM 호출 없음.</b>
 *
 * <p>Lucene {@link FuzzySuggester}(글자 단위 편집거리)로 입력 일부/오타에도 FAQ 후보를 제안한다.
 * 색인 항목 = 각 FAQ의 (1) 질문 전체 (2) {@link KoreanAnalyzer}(Nori) 키워드. payload에 {@code id+question}을
 * 함께 담아, 어떤 단어/오타로 매칭돼도 그 FAQ의 id·질문을 바로 돌려준다(중복 FAQ는 합침).
 *
 * <p>FAQ 20행 규모라 색인은 <b>첫 호출 때 1회 빌드 후 메모리 캐시</b>(영속화 없음).
 * FAQ 변경 시 {@link #rebuild()}로 원자 교체(조회 무중단).
 */
@Component
public class SuggesterForRag {

    /** 자동완성 후보 1건. id(PK)로 클릭 시 바로 해당 FAQ를 쓸 수 있다. */
    public record Suggestion(String id, String question) {}

    private record Entry(BytesRef term, BytesRef payload) {}

    /** payload 인코딩 구분자(일반 텍스트에 안 나오는 제어문자). */
    private static final String SEP = "";

    private final ChatBotRepository chatBotRepository;
    private volatile FuzzySuggester suggester;

    public SuggesterForRag(ChatBotRepository repositoryForRag) {
        this.chatBotRepository = repositoryForRag;
    }

    /** 입력(일부/오타 허용)으로 FAQ 후보 최대 num개 (id+question). */
    public List<Suggestion> suggest(String input, int num) {
        if (input == null || input.isBlank() || num <= 0) {
            return List.of();
        }
        try {
            // 색인 키가 공백 제거형이므로 질의도 동일하게 공백을 없애 매칭(오타 보정이 공백을 못 넘는 문제 회피).
            List<LookupResult> results = built().lookup(stripSpaces(input.trim()), false, num * 5);
            Map<String, String> byId = new LinkedHashMap<>();   // id → question (관련도순, 첫 등장 보존)
            for (LookupResult r : results) {
                String[] idQ = r.payload.utf8ToString().split(SEP, 2);
                if (idQ.length == 2 && !byId.containsKey(idQ[0])) {
                    byId.put(idQ[0], idQ[1]);
                    if (byId.size() >= num) {
                        break;
                    }
                }
            }
            List<Suggestion> out = new ArrayList<>(byId.size());
            byId.forEach((id, q) -> out.add(new Suggestion(id, q)));
            return out;
        } catch (IOException e) {
            throw new UncheckedIOException("자동완성 조회 실패: " + input, e);
        }
    }

    /** FAQ 변경 후 호출 → 새로 빌드해 원자적으로 교체(조회 무중단). */
    public synchronized void rebuild() {
        this.suggester = build();
    }

    private FuzzySuggester built() {
        FuzzySuggester local = suggester;
        if (local != null) {
            return local;
        }
        synchronized (this) {
            if (suggester == null) {
                suggester = build();
            }
            return suggester;
        }
    }

    private FuzzySuggester build() {
        FuzzySuggester s = newSuggester();
        try {
            s.build(iteratorOf(collectEntries()));
        } catch (IOException e) {
            throw new UncheckedIOException("자동완성 색인 빌드 실패", e);
        }
        return s;
    }

    /**
     * (id,question)만 읽어(임베딩 제외) 질문+키워드 항목 생성. payload=id+SEP+question(표시는 원본 유지).
     * <p>색인 <b>키(term)는 공백을 모두 제거</b>한다 — FuzzySuggester가 공백을 사이에 둔 오타를 잘 못 잡기 때문.
     */
    private List<Entry> collectEntries() {
        List<Entry> entries = new ArrayList<>();
        chatBotRepository.forEachIdQuestion((id, question) -> {
            BytesRef payload = new BytesRef(id + SEP + question);   // 표시용: 원본 질문(공백 유지)
            addTerm(entries, question, payload);                   // 질문 전체(공백 제거 키)
            for (String token : tokenize(question)) {              // 키워드(부분/중간 매칭용)
                addTerm(entries, token, payload);
            }
        });
        return entries;
    }

    /**
     * [비교용] 위 {@link #collectEntries()}와 결과는 같지만 방식이 다른 버전.
     * <p><b>BiConsumer(push):</b> Repository가 행을 돌며 내 로직을 콜백으로 호출 → 중간 리스트 없음.
     * <b>List(pull):</b> findAll로 전부 받아온 뒤 호출자가 직접 for문을 돈다.
     * <p>차이점: ① findAll은 임베딩(float[1536])까지 다 로드해 메모리 낭비 ② 전체를 리스트에
     * 모았다가 다시 순회 → 컬렉션을 두 번 들고 있음. FAQ 20행이면 실무 차이는 미미하지만 의도가 다르다.
     */
    private List<Entry> collectEntriesViaList() {
        List<Entry> entries = new ArrayList<>();
        List<ChatBotFaqDocDTO> docs = chatBotRepository.findAll();   // 전체를 리스트로 한 번에 받아옴(임베딩 포함)
        for (ChatBotFaqDocDTO doc : docs) {                          // 받아온 리스트를 호출자가 직접 순회
            String id = doc.getId();
            String question = doc.getQuestion();
            BytesRef payload = new BytesRef(id + SEP + question);
            addTerm(entries, question, payload);
            for (String token : tokenize(question)) {
                addTerm(entries, token, payload);
            }
        }
        return entries;
    }

    /** 공백 제거한 키로 색인 항목 추가(빈 키는 제외). */
    private static void addTerm(List<Entry> entries, String text, BytesRef payload) {
        String key = stripSpaces(text);
        if (!key.isEmpty()) {
            entries.add(new Entry(new BytesRef(key), payload));
        }
    }

    /** 모든 공백(스페이스·탭 등) 제거. */
    private static String stripSpaces(String s) {
        return s == null ? "" : s.replaceAll("\\s+", "");
    }

    /** 질문을 KoreanAnalyzer(Nori)로 토큰화 — 부분/중간 단어 매칭용 키워드. (빌드 시 1회만 호출) */
    private static List<String> tokenize(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        List<String> tokens = new ArrayList<>();
        try (Analyzer analyzer = new KoreanAnalyzer();
             TokenStream ts = analyzer.tokenStream("q", text)) {
            CharTermAttribute term = ts.addAttribute(CharTermAttribute.class);
            ts.reset();
            while (ts.incrementToken()) {
                tokens.add(term.toString());
            }
            ts.end();
        } catch (IOException e) {
            throw new UncheckedIOException("자동완성 토큰화 실패: " + text, e);
        }
        return tokens;
    }

    /** build·lookup이 동일 설정을 쓰도록 한 곳에서 생성. (unicodeAware=true: 한글 글자 단위 편집거리) */
    private static FuzzySuggester newSuggester() {
        KeywordAnalyzer analyzer = new KeywordAnalyzer();
        return new FuzzySuggester(
                new ByteBuffersDirectory(), "faq-suggest", analyzer, analyzer,
                AnalyzingSuggester.EXACT_FIRST | AnalyzingSuggester.PRESERVE_SEP,
                256, -1, true,
                FuzzySuggester.DEFAULT_MAX_EDITS, FuzzySuggester.DEFAULT_TRANSPOSITIONS,
                FuzzySuggester.DEFAULT_NON_FUZZY_PREFIX, FuzzySuggester.DEFAULT_MIN_FUZZY_LENGTH,
                true);   // unicodeAware
    }

    /** entries 리스트를 Lucene이 요구하는 InputIterator(가중치/페이로드 포함)로 감싼다. */
    private static InputIterator iteratorOf(List<Entry> entries) {
        return new InputIterator() {
            private int i = -1;

            @Override
            public BytesRef next() {
                i++;
                return i < entries.size() ? entries.get(i).term() : null;
            }

            @Override
            public long weight() {
                return 1L;
            }

            @Override
            public BytesRef payload() {
                return entries.get(i).payload();
            }

            @Override
            public boolean hasPayloads() {
                return true;
            }

            @Override
            public Set<BytesRef> contexts() {
                return null;
            }

            @Override
            public boolean hasContexts() {
                return false;
            }
        };
    }
}