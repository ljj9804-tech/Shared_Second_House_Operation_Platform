package com.busanit401.spring_back.domain.repository;

import com.busanit401.spring_back.domain.entity.StayAccommodation;
import com.busanit401.spring_back.enums.StayAccommodationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StayAccommodationRepository extends JpaRepository<StayAccommodation, Long> {

    // 상태별 숙소 목록 조회 (AVAILABLE / MAINTENANCE)
    List<StayAccommodation> findByStatus(StayAccommodationStatus status);
}