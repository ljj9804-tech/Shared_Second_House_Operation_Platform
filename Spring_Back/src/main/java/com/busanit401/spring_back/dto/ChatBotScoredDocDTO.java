package com.busanit401.spring_back.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 점수가 매겨진 검색 결과 1건. 융합(가중합/RRF)은 이 점수를 입력으로 쓴다. (DTO) */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatBotScoredDocDTO {

    private String id;
    private double score;
}