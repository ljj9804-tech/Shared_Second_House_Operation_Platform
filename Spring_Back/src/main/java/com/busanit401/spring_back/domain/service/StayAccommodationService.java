package com.busanit401.spring_back.domain.service;

import com.busanit401.spring_back.dto.StayAccommodationRequestDto;
import com.busanit401.spring_back.dto.StayAccommodationResponseDto;

import java.util.List;

public interface StayAccommodationService {
//    실행 흐름:
//    Controller → Service(interface) → ServiceImpl 순서로 호출
//    interface로 분리 → 구현체 교체 용이, 테스트 용이

    // 숙소 목록 조회
    List<StayAccommodationResponseDto> getAccommodations();

    // 숙소 상세 조회
    StayAccommodationResponseDto getAccommodation(Long id);

    // 숙소 등록 (관리자)
    StayAccommodationResponseDto createAccommodation(StayAccommodationRequestDto requestDto);

    // 숙소 수정 (관리자)
    StayAccommodationResponseDto updateAccommodation(Long id, StayAccommodationRequestDto requestDto);

    // 숙소 삭제 (관리자)
    void deleteAccommodation(Long id);
}