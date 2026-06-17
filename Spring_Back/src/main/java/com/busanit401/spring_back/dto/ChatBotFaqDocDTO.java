package com.busanit401.spring_back.dto;

import lombok.*;

/**
 * 검색 대상 문서 1건 = FAQ 한 행(sh_faq_docs / faq_vector2). (JPA 엔티티 아님 — 조회용 DTO)
 *
 * @see #searchableText()
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatBotFaqDocDTO {

    /** FAQ id(문자열). 평가셋의 answer_doc_ids 와 매칭되는 키. */
    private String id;
    /** 사용자 질문 텍스트 (검색 대상). */
    private String question;
    /** 답변 텍스트 (RAG가 최종 반환할 내용). */
    private String answer;
    /** Gemini 임베딩 벡터. 임베딩 그룹만 사용, lexical 그룹은 무시. sh_faq_docs=1536, faq_vector2=3072. */
    @ToString.Exclude
    private float[] embedding;

    /** 검색 본문: 질문 + 답변을 합친 텍스트 (lexical 인덱싱용). */
    public String searchableText() {
        return question + " " + answer;
    }
}