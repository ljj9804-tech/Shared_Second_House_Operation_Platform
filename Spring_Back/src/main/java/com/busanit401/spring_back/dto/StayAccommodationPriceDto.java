package com.busanit401.spring_back.dto;

import com.busanit401.spring_back.domain.entity.StayAccommodationPrice;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StayAccommodationPriceDto {

//    실행 흐름:
//    숙소 상세 조회 시 → StayAccommodationResponseDto 안에 prices 리스트로 포함되어 응답
//    프론트에서 팀수 + 개월수 선택 → 이 DTO의 할인율로 월세 자동 계산
//    월세는 StayAccommodationResponseDto.monthlyPrice 사용

    private Long id;                // 가격 구간 ID
    private Integer minMonths;      // 최소 계약 월 수
    private Integer maxMonths;      // 최대 계약 월 수 (null이면 제한 없음)
    private Double discountRate;    // 할인율 (예: 0.0, 0.07, 0.15)

    // Entity → DTO 변환 메서드
    public static StayAccommodationPriceDto from(StayAccommodationPrice price) {
        return StayAccommodationPriceDto.builder()
                .id(price.getId())
                .minMonths(price.getMinMonths())
                .maxMonths(price.getMaxMonths())
                .discountRate(price.getDiscountRate())
                .build();
    }
}