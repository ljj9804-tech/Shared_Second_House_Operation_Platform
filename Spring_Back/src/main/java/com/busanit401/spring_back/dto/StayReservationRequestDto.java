package com.busanit401.spring_back.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StayReservationRequestDto {

//    실행 흐름:
//    구독 중인 유저가 예약 캘린더에서 날짜 선택 → 이 DTO로 데이터 전송
//    Controller에서 받아서 → Service에서 Entity로 변환 후 저장

    private Long accommodationId;   // 예약할 숙소 ID
    private Long userId;            // 예약한 유저 ID
    private LocalDate startDate;    // 예약 시작일
    private LocalDate endDate;      // 예약 종료일
}