package com.busanit401.spring_back.domain.service;

import com.busanit401.spring_back.dto.StayReservationRequestDto;
import com.busanit401.spring_back.dto.StayReservationResponseDto;

import java.util.List;

public interface StayReservationService {

//    실행 흐름:
//    Controller → Service(interface) → ServiceImpl 순서로 호출

    // 예약 목록 조회 (로그인한 유저의 예약 목록)
    List<StayReservationResponseDto> getReservations(Long userId);

    // 예약 생성
    StayReservationResponseDto createReservation(Long userId, StayReservationRequestDto requestDto);

    // 예약 취소
    void cancelReservation(Long reservationId, Long userId);
}