package com.busanit401.spring_back.dto;

import com.busanit401.spring_back.domain.entity.StayStory;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StayStoryResponseDto {

//    실행 흐름:
//    Service에서 스토리 Entity 조회 → 이 DTO로 변환 → Controller에서 프론트로 응답
//    숙소 상세 페이지 [섹션6] 스토리 01~04 표시에 사용

    private Long id;                // 스토리 ID
    private Integer orderNum;       // 표시 순서 (1, 2, 3, 4)
    private String title;           // 스토리 제목
    private String content;         // 스토리 본문 텍스트
    private String imageUrl;        // 스토리 이미지 URL 1장

    // Entity → DTO 변환 메서드
    public static StayStoryResponseDto from(StayStory story) {
        return StayStoryResponseDto.builder()
                .id(story.getId())
                .orderNum(story.getOrderNum())
                .title(story.getTitle())
                .content(story.getContent())
                .imageUrl(story.getImageUrl())
                .build();
    }
}