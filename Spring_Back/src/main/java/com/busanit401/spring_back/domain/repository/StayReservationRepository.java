package com.busanit401.spring_back.domain.repository;

import com.busanit401.spring_back.domain.entity.StayReservation;
import com.busanit401.spring_back.enums.StayReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StayReservationRepository extends JpaRepository<StayReservation, Long> {

    // 숙소별 예약 목록 조회
    List<StayReservation> findByStayAccommodationId(Long accommodationId);

    // 유저별 예약 목록 조회
    List<StayReservation> findByUserId(Long userId);

    // 숙소별 확정된 예약 목록 조회 (캘린더 비활성 날짜 표시용)
    List<StayReservation> findByStayAccommodationIdAndStatus(Long accommodationId, StayReservationStatus status);
}