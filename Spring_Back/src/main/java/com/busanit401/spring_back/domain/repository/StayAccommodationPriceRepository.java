package com.busanit401.spring_back.domain.repository;

import com.busanit401.spring_back.domain.entity.StayAccommodationPrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StayAccommodationPriceRepository extends JpaRepository<StayAccommodationPrice, Long> {

    // 공통 할인율 구간 목록 조회 (orderNum 없으므로 전체 조회)
    List<StayAccommodationPrice> findAll();
}