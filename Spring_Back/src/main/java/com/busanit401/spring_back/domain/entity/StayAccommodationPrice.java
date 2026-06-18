package com.busanit401.spring_back.domain.entity;

import com.busanit401.spring_back.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sh_stay_accommodation_price")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class StayAccommodationPrice extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "price_id")
    private Long id;

    // accommodationId 제거 → 공통 할인율 테이블로 변경
    // monthlyPrice 제거 → StayAccommodation으로 이동

    @Column(nullable = false)
    private Integer minMonths;      // 최소 계약 월 수

    private Integer maxMonths;      // 최대 계약 월 수 (null이면 제한 없음)

    @Column(nullable = false)
    private Double discountRate;    // 할인율 (예: 1-1개월 : 0.0, 3-5개월 : 0.07, 6개월이상 : 0.15)
}