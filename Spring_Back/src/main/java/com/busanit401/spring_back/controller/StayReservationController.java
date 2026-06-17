//    실행 흐름:
//    프론트 요청 → Controller → Service → 응답
//    GET /api/stay/reservations              → 내 예약 목록
//    POST /api/stay/reservations             → 예약 생성
//    PATCH /api/stay/reservations/{id}/cancel → 예약 취소


package com.busanit401.spring_back.controller;

import com.busanit401.spring_back.domain.service.StayReservationService;
import com.busanit401.spring_back.dto.StayReservationRequestDto;
import com.busanit401.spring_back.dto.StayReservationResponseDto;
import com.busanit401.spring_back.security.auth.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RestController
@RequestMapping("/api/stay/reservations")
@RequiredArgsConstructor
@Tag(name = "Stay Reservation", description = "숙소 예약 관리 API")
public class StayReservationController {

    private final StayReservationService reservationService;

    // 내 예약 목록 조회 (로그인한 유저 기준)
    @GetMapping
    @Operation(summary = "내 예약 목록 조회", description = "로그인한 유저의 예약 목록을 조회합니다.")
    public ResponseEntity<List<StayReservationResponseDto>> getReservations(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        // 토근인증 방법 되돌리기
//        log.info("GET /api/stay/reservations - userId: {}", userDetails.getId());
//        return ResponseEntity.ok(reservationService.getReservations(userDetails.getId()));
        // TODO [인증]: JWT 토큰에서 실제 userId 추출로 교체
        Long userId = userDetails != null ? userDetails.getId() : 1L;
        log.info("GET /api/stay/reservations - userId: {}", userId);
        return ResponseEntity.ok(reservationService.getReservations(userId));
    }

    // 예약 생성
    @PostMapping
    @Operation(summary = "예약 생성", description = "숙소를 예약합니다.")
    public ResponseEntity<StayReservationResponseDto> createReservation(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody StayReservationRequestDto requestDto) {
        // 토근인증 방법 되돌리기
//        log.info("POST /api/stay/reservations - userId: {}, accommodationId: {}", userDetails.getId(), requestDto.getAccommodationId());
//        return ResponseEntity.ok(reservationService.createReservation(userDetails.getId(), requestDto));
        // TODO [인증]: JWT 토큰에서 실제 userId 추출로 교체
        Long userId = userDetails != null ? userDetails.getId() : 1L;
        log.info("POST /api/stay/reservations - userId: {}", userId);
        return ResponseEntity.ok(reservationService.createReservation(userId, requestDto));
    }

    // 예약 취소
    @PatchMapping("/{id}/cancel")
    @Operation(summary = "예약 취소", description = "예약을 취소합니다.")
    public ResponseEntity<Void> cancelReservation(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        // 토근인증 방법 되돌리기
//        log.info("PATCH /api/stay/reservations/{}/cancel - userId: {}", id, userDetails.getId());
//        reservationService.cancelReservation(id, userDetails.getId());
//        return ResponseEntity.noContent().build();
        // TODO [인증]: JWT 토큰에서 실제 userId 추출로 교체
        Long userId = userDetails != null ? userDetails.getId() : 1L;
        log.info("PATCH /api/stay/reservations/{}/cancel - userId: {}", id, userId);
        reservationService.cancelReservation(id, userId);
        return ResponseEntity.noContent().build();
    }
}