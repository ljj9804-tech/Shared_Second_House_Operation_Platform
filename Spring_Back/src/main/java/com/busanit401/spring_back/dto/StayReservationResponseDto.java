package com.busanit401.spring_back.dto;

import com.busanit401.spring_back.domain.entity.StayReservation;
import com.busanit401.spring_back.enums.StayReservationStatus;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StayReservationResponseDto {

//    실행 흐름:
//    Service에서 예약 Entity 조회 → 이 DTO로 변환 → Controller에서 프론트로 응답
//    내 예약 목록 페이지 (/my/reservations) 에서 사용

    private Long id;                        // 예약 ID
    private Long accommodationId;           // 숙소 ID
    private String accommodationName;       // 숙소명
    private String accommodationAddress;    // 숙소 주소
    private LocalDate startDate;            // 예약 시작일
    private LocalDate endDate;              // 예약 종료일
    private StayReservationStatus status;   // CONFIRMED / CANCELLED

    // Entity → DTO 변환 메서드
    public static StayReservationResponseDto from(StayReservation reservation) {
        return StayReservationResponseDto.builder()
                .id(reservation.getId())
                .accommodationId(reservation.getStayAccommodation().getId())
                .accommodationName(reservation.getStayAccommodation().getName())
                .accommodationAddress(reservation.getStayAccommodation().getAddress())
                .startDate(reservation.getStartDate())
                .endDate(reservation.getEndDate())
                .status(reservation.getStatus())
                .build();
    }
}