package com.busanit401.spring_back.domain.service.rag;

import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 임베딩은 문장의 의미를 파악하는 방식이라 문장 전체를 제미나이로 임베딩 함
 * embed는 캐시에 질의 요청문의 임베딩 벡터를 등록해 같은 요청문은 캐시에서 가져오고, 없으면 제미나이 호출을 함
 * <p>⚠️ 문서 임베딩과 동일 설정이어야 함: model=gemini-embedding-2, dim=1536, task_type 없음.
 */
public class GeminiQueryEmbeddingProviderForRag implements QueryEmbeddingProviderForRag {

    private final GeminiEmbeddingClientForRag client;
    private final int dim;
    private final Map<String, float[]> cache = new ConcurrentHashMap<>();

    /** 공용 {@link WebClient}(스프링 빈)를 주입받는 생성자. */
    public GeminiQueryEmbeddingProviderForRag(WebClient webClient,
                                              String apiKey, String model, int dim, String taskType) {
        this.client = new GeminiEmbeddingClientForRag(webClient, apiKey, model, dim, taskType);
        this.dim = dim;
    }

    /** 독립 실행·테스트용: 내부에서 WebClient를 자체 생성. */
    public GeminiQueryEmbeddingProviderForRag(String apiKey, String model, int dim, String taskType) {
        this.client = new GeminiEmbeddingClientForRag(apiKey, model, dim, taskType);
        this.dim = dim;
    }

    @Override
    public float[] embed(String query) {    //문장의 의미를 파악하는 방식이라 문장 전체를 제미나이로 임베딩 함
        return cache.computeIfAbsent(query, q -> {  //캐시에 같은 질의가 있으면 캐시값 반환, 없으면 Gemini 호출
            float[] full = client.embed(q); //질의 요청문의 vector배열(Gemini 호출 응답)
            // 문서가 values[:dim]로 잘랐으므로 동일하게 맞춤(보통 이미 dim 길이)
            return full.length > dim ? Arrays.copyOf(full, dim) : full; //dim의 길이가 vector의 길이보다 길면 dim에 맞게 잘라서 반환
        });
    }
}