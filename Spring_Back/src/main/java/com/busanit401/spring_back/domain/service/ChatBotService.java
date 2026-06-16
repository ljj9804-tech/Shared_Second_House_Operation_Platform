package com.busanit401.spring_back.domain.service;

import com.busanit401.spring_back.domain.service.rag.SuggesterForRag;

import java.util.List;

/**
 * 운영용 FAQ 검색 서비스의 공개 계약. 컨트롤러는 이 인터페이스에만 의존하고,
 * 코퍼스 로드·검색기 조립·캐싱 같은 구현은 {@code JoldoServiceImpl}이 담당한다.
 */
public interface ChatBotService {

    /** 검색 결과 1건(점수+질문+답변). 응답 포맷은 컨트롤러가 결정. */
    record SearchHit(String id, double score, String question, String answer) {}

    /** RAG 최종 응답: 질의 + 생성된 답변 + 근거로 쓴 FAQ들. */
    record AnswerResult(String query, String answer, List<SearchHit> sources) {}

    /** LuceneBM25(단어 매칭)로 상위 limit개 검색. Gemini 키 불필요. */
    List<SearchHit> luceneSearch(String query, int limit);

    /** 임베딩(의미 매칭)으로 상위 limit개 검색. 질의 임베딩에 Gemini 키 필요(요청마다 전달). */
    List<SearchHit> embeddingSearch(String query, int limit);

    /** LuceneBM25(단어)+임베딩(의미)을 <b>가중합 융합</b>해 상위 limit개 검색. Gemini 키 필요. */
    List<SearchHit> fusionSearch(String query, int limit);

    /**
     * RAG 답변 생성: 가중합 융합으로 상위 topK FAQ를 추려 근거로 묶고,
     * {@code gemini-2.5-flash}가 그 근거만 바탕으로 최종 답변을 생성한다. Gemini 키 필요.
     */
    AnswerResult answer(String query, int topK);

    /** 추천검색어 자동완성(오타 보정). 임베딩·LLM 비용 0(Lucene). */
    List<SuggesterForRag.Suggestion> suggest(String input, int limit);

    /** id로 FAQ 단건(질문+답변) 조회. 자동완성 후보 클릭 시 사용. 없으면 예외. */
    SearchHit faqById(String id);
}