package com.busanit401.spring_back.domain.service.rag;
/**
 * 질의 텍스트 → 임베딩 벡터. 구현은 (a) 사전 캐시 로드 (b) Gemini 실시간 호출 등.
 */
public interface QueryEmbeddingProviderForRag {

    /** 질의 임베딩. 캐시에 없거나 임베딩 불가하면 null. */
    float[] embed(String query);
}
