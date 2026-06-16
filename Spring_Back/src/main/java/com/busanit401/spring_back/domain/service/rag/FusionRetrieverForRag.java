package com.busanit401.spring_back.domain.service.rag;

import com.busanit401.spring_back.dto.ChatBotScoredDocDTO;

import java.util.*;

/**
 * 여러 검색기의 결과를 융합하는 검색기 (B안).
 * lucene와 embedding방식의 계산기로 각가의 점수를 내고, 정규화 점수로 만들어 가중치를 이용해 가중합을 진행
 * 가중합의 경우 lucene의 점수의 범위와 embedding의 점수의 범위가 다르기때문에 점수의 정규화를 해야함
 * lucene에서 추려낸 poolSize개의 문서와 embedding에서 추려낸 poolSize개의 문서가 다를 경우 존재할수도 있는데
 * 겹치는게 n개라면 총 poolSize * 2 -n개의 문서가 추려져서 퓨전을 진행 할거고, 해당 가중치와 정규화된 곱을 해서 겹치는 문서의 점수는 두 방식의 합으로 정한다.
 * 정규화 점수 = (점수-최소)/(최대-최소)
 * 이후 k개의 데이터를 추려내서 (id+점수)리스트를 반환
 * poolSize = k의 2~10배
 */
public class FusionRetrieverForRag implements ScoringRetrieverForRag {

    public enum Method { RRF, WEIGHTED_SUM }

    public static final int DEFAULT_RRF_K = 60;
    private static final int DEFAULT_POOL = 15;   // 각 소스에서 가져올 후보 수(20행이라 사실상 전체)

    private final String name;
    private final List<ScoringRetrieverForRag> sources;
    private final Method method;
    private final double[] weights;   // WEIGHTED_SUM: sources와 길이 동일. RRF면 무시
    private final int rrfK;
    private final int poolSize;

    /** RRF 융합(기본 k=60). */
    public static FusionRetrieverForRag rrf(String name, List<ScoringRetrieverForRag> sources) {
        return new FusionRetrieverForRag(name, sources, Method.RRF, null, DEFAULT_RRF_K, DEFAULT_POOL);
    }

    /** 가중합 융합. weights는 sources와 같은 길이. */
    public static FusionRetrieverForRag weightedSum(String name, List<ScoringRetrieverForRag> sources, double[] weights) {
        return new FusionRetrieverForRag(name, sources, Method.WEIGHTED_SUM, weights, DEFAULT_RRF_K, DEFAULT_POOL);
    }

    public FusionRetrieverForRag(String name, List<ScoringRetrieverForRag> sources, Method method,
                                 double[] weights, int rrfK, int poolSize) {
        if (sources == null || sources.isEmpty()) {
            throw new IllegalArgumentException("융합할 소스가 1개 이상 필요");
        }
        if (method == Method.WEIGHTED_SUM) {
            if (weights == null || weights.length != sources.size()) {  //2가지의 검색기(소스)라면 소스하나에 대한 가중치이므로 각각의 가중치 하나씩해서 2개가 들어와야 함
                throw new IllegalArgumentException("가중치 개수가 소스 개수와 달라요");
            }
        }
        this.name = name;
        this.sources = List.copyOf(sources);
        this.method = method;
        this.weights = weights == null ? null : weights.clone();
        this.rrfK = rrfK;
        this.poolSize = poolSize;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public List<ChatBotScoredDocDTO> searchScored(String query, int k) {
        List<List<ChatBotScoredDocDTO>> rankings = new ArrayList<>();
        for (ScoringRetrieverForRag s : sources) {
            rankings.add(s.searchScored(query, poolSize));  //각각의 검색기에서 검색한 점수 높은 순으로 poolSize만큼 가져옴
        }
        Map<String, Double> fused = (method == Method.RRF) ? fuseRrf(rankings) : fuseWeighted(rankings);

        List<ChatBotScoredDocDTO> result = new ArrayList<>();
        for (Map.Entry<String, Double> e : fused.entrySet()) {
            result.add(ChatBotScoredDocDTO.builder().id(e.getKey()).score(e.getValue()).build());
        }
        result.sort(Comparator.comparingDouble(ChatBotScoredDocDTO::getScore).reversed()
                .thenComparing(ChatBotScoredDocDTO::getId));
        return result.subList(0, Math.min(k, result.size()));
    }

    /**
     * 순위 융합(RRF): 점수는 무시하고 각 소스에서의 "등수"만 사용.
     * 한 문서가 어떤 소스에서 rank등(0-base)이면 1/(rrfK + rank+1)점을 받고, 모든 소스 점수를 합산.
     * 여러 소스에서 두루 상위면 합이 커진다. 스케일 정규화가 필요 없는 게 장점.
     */
    private Map<String, Double> fuseRrf(List<List<ChatBotScoredDocDTO>> rankings) {
        Map<String, Double> fused = new HashMap<>();
        for (List<ChatBotScoredDocDTO> ranking : rankings) {
            for (int rank = 0; rank < ranking.size(); rank++) {
                // rank+1 = 1-base 등수. 1등이 가장 큰 점수, 아래로 갈수록 작아짐.
                fused.merge(ranking.get(rank).getId(), 1.0 / (rrfK + rank + 1), Double::sum);
            }
        }
        return fused;
    }

    /**
     * 점수 융합(가중합): 소스마다 점수 스케일이 달라서(BM25 0~수십, 임베딩) 그대로 더하면
     * 큰 쪽이 다 먹는다. 그래서 소스별로 Min-Max 정규화(최솟값→0, 최댓값→1)한 뒤 가중치를 곱해 합산.
     */
    private Map<String, Double> fuseWeighted(List<List<ChatBotScoredDocDTO>> rankings) {
        Map<String, Double> fused = new HashMap<>();
        for (int i = 0; i < rankings.size(); i++) {
            List<ChatBotScoredDocDTO> ranking = rankings.get(i);
            double w = weights[i];                // 이 소스의 가중치

            // 이 소스 후보들의 점수 최소/최대 (정규화 기준)
            double min = Double.POSITIVE_INFINITY;
            double max = Double.NEGATIVE_INFINITY;
            for (ChatBotScoredDocDTO d : ranking) {
                min = Math.min(min, d.getScore());
                max = Math.max(max, d.getScore());
            }
            double range = max - min;   //정규화 할때 사용 소스별로 1번씩 돈다.

            for (ChatBotScoredDocDTO d : ranking) {
                // 정규화 점수 = (점수-최소)/(최대-최소) → [0,1]
                // 후보가 1개거나 점수가 전부 같으면(range=0) 나눗셈 불가 → 1.0으로 취급
                double norm = (range == 0.0) ? 1.0 : (d.getScore() - min) / range;
                fused.merge(d.getId(), w * norm, Double::sum);   // 가중치 곱해 누적
            }
        }
        return fused;
    }
}
