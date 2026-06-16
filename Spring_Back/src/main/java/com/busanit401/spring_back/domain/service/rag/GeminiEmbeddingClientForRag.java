package com.busanit401.spring_back.domain.service.rag;

import com.busanit401.spring_back.exception.ChatBotGeminiApiException;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * webClient로 임베딩 요청형식에 맞춰 요청을 보내고, 응답형식에 맞춰 데이터를 응답을 받아서
 * embed를 이용해 질의 요청문의 vector 배열을 반환
 * 업스트림(Gemini) HTTP 오류는 ChatBotGeminiApiException으로 변환해 던짐(전역 핸들러가 처리).
 */
public class GeminiEmbeddingClientForRag {

    /** ConfigForRag의 WebClient baseUrl 이후 상대 경로(모델명은 path 변수). */
    private static final String BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models";

    private final WebClient webClient;
    private final String apiKey;
    private final String model;
    private final int outputDimensionality;
    private final String taskType;

    /** 공용 {@link WebClient}(스프링 빈)를 주입받는 생성자. */
    public GeminiEmbeddingClientForRag(WebClient webClient, String apiKey, String model,
                                       int outputDimensionality, String taskType) {
        this.webClient = webClient;
        this.apiKey = apiKey;
        this.model = model;
        this.outputDimensionality = outputDimensionality;
        this.taskType = taskType;
    }

    /** 독립 실행·테스트용: WebClient를 자체 생성(빈 주입 없이도 동작). */
    public GeminiEmbeddingClientForRag(String apiKey, String model, int outputDimensionality, String taskType) {
        this(WebClient.builder().baseUrl(BASE_URL).build(), apiKey, model, outputDimensionality, taskType);
    }

    /**
     * 질의 텍스트를 임베딩해 {@code float[]} 벡터로 반환.
     * <pre>
     * 요청:  { "model": "models/gemini-embedding-2",
     *         "content": { "parts": [ { "text": "강아지 데려가도 돼요?" } ] },
     *         "outputDimensionality": 1536 }
     * 응답:  { "embedding": { "values": [0.01, -0.03, ...] } }
     * </pre>
     */
    public float[] embed(String text) {
        try {
            JsonNode response = webClient.post()    //Spring mvc(동기)는 요청마다 스레드를 하나씩 배정하는 동기 방식이고 webClient는 비동기적이라 이 프로젝트에서는 동기적으로 진행함
                    .uri(BASE_URL + "/{model}:embedContent?key={key}", model, apiKey)    //공용 WebClient(baseUrl 없음)라 절대 URL로 직접 지정
                    .bodyValue(buildRequestBody(text))  //요청
                    .retrieve()
                    .bodyToMono(JsonNode.class) //미래에 올 값 형식
                    .block();   //동기적으로 응답까지 기다림
            return extractValues(response); //응답값
        } catch (WebClientResponseException e) {
            throw new ChatBotGeminiApiException(
                    HttpStatus.BAD_GATEWAY,
                    resolveGeminiErrorCode(e),
                    buildGeminiHttpErrorMessage(e));
        }
    }

    private Map<String, Object> buildRequestBody(String text) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("model", "models/" + model);
        body.put("content", Map.of("parts", List.of(Map.of("text", text))));
        if (taskType != null && !taskType.isBlank()) {
            body.put("taskType", taskType);   // 문서=RETRIEVAL_DOCUMENT, 질의=RETRIEVAL_QUERY (맞추면 정확도 ↑)
        }
        if (outputDimensionality > 0) {
            body.put("outputDimensionality", outputDimensionality);
        }
        return body;
    }

    private float[] extractValues(JsonNode response) {
        if (response == null) {
            throw new ChatBotGeminiApiException(HttpStatus.BAD_GATEWAY, "EMPTY_RESPONSE",
                    "Gemini 임베딩 응답이 비어 있습니다.");
        }
        JsonNode values = response.path("embedding").path("values");
        if (!values.isArray() || values.isEmpty()) {
            throw new ChatBotGeminiApiException(HttpStatus.BAD_GATEWAY, "INVALID_RESPONSE",
                    "Gemini 임베딩 응답에 values가 없습니다.");
        }
        float[] vec = new float[values.size()];
        for (int i = 0; i < vec.length; i++) {
            vec[i] = (float) values.get(i).asDouble();
        }
        return vec;
    }

    private String resolveGeminiErrorCode(WebClientResponseException e) {
        String body = e.getResponseBodyAsString();
        int status = e.getStatusCode().value();

        if (body != null && body.contains("no longer available to new users")) {
            return "MODEL_UNAVAILABLE";
        }
        if (status == 400 || status == 401 || status == 403) {
            return "INVALID_API_KEY_OR_REQUEST";
        }
        if (status == 429) {
            return "RATE_LIMITED";
        }
        if (e.getStatusCode().is5xxServerError()) {
            return "UPSTREAM_TEMPORARY_ERROR";
        }
        return "GEMINI_HTTP_ERROR";
    }

    private String buildGeminiHttpErrorMessage(WebClientResponseException e) {
        String body = e.getResponseBodyAsString();
        int status = e.getStatusCode().value();

        if (body != null && body.contains("no longer available to new users")) {
            return "현재 설정된 Gemini 모델을 사용할 수 없습니다. 최신 지원 모델명으로 변경해주세요.";
        }
        if (status == 400 || status == 401 || status == 403) {
            return "Gemini API 키가 올바르지 않거나 요청이 잘못되었습니다.";
        }
        if (status == 429) {
            return "AI 요청이 잠시 많습니다. 잠시 후 다시 시도해주세요.";
        }
        if (e.getStatusCode().is5xxServerError()) {
            return "Gemini 서비스가 일시적으로 불안정합니다. 잠시 후 다시 시도해주세요.";
        }
        return "Gemini 임베딩 호출에 실패했습니다. upstream status=" + status;
    }
}