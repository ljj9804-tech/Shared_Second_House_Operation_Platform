package com.busanit401.spring_back.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StayStoryRequestDto {

//    실행 흐름:
//    관리자가 숙소 스토리 등록/수정 시 → 이 DTO로 데이터 전송
//    Controller에서 받아서 → Service에서 Entity로 변환 후 저장

    private Long accommodationId;   // 어떤 숙소의 스토리인지
    private Integer orderNum;       // 표시 순서 (1, 2, 3, 4)
    private String title;           // 스토리 제목
    private String content;         // 스토리 본문 텍스트
    private String imageUrl;        // 스토리 이미지 URL 1장
}