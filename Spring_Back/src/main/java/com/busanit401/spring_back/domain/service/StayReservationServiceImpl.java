//    실행 흐름:
//    Controller에서 호출 → Repository로 DB 조회/저장 → DTO로 변환 후 반환
//    예약 생성 시 → 날짜 중복 체크 후 저장
//    예약 취소 시 → 본인 예약인지 확인 후 CANCELLED 상태로 변경

package com.busanit401.spring_back.domain.service;

import com.busanit401.spring_back.domain.User;
import com.busanit401.spring_back.domain.entity.StayAccommodation;
import com.busanit401.spring_back.domain.entity.StayReservation;
import com.busanit401.spring_back.domain.repository.StayAccommodationRepository;
import com.busanit401.spring_back.domain.repository.StayReservationRepository;
import com.busanit401.spring_back.domain.repository.UserRepository;
import com.busanit401.spring_back.dto.StayReservationRequestDto;
import com.busanit401.spring_back.dto.StayReservationResponseDto;
import com.busanit401.spring_back.exception.BusinessException;
import com.busanit401.spring_back.exception.ErrorCode;
import com.busanit401.spring_back.enums.StayReservationStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StayReservationServiceImpl implements StayReservationService {

    private final StayReservationRepository reservationRepository;
    private final StayAccommodationRepository accommodationRepository;
    private final UserRepository userRepository;

    // 예약 목록 조회 (로그인한 유저의 예약 목록)
    @Override
    public List<StayReservationResponseDto> getReservations(Long userId) {
        log.info("예약 목록 조회 시작 - userId: {}", userId);
        List<StayReservationResponseDto> result = reservationRepository.findByUserId(userId).stream()
                .map(StayReservationResponseDto::from)
                .sorted(Comparator.comparingInt((StayReservationResponseDto dto) -> dto.getStatus().ordinal())
                        .thenComparing(StayReservationResponseDto::getStartDate))
                .collect(Collectors.toList());
        log.info("예약 목록 조회 완료 - 총 {}개", result.size());
        return result;
    }

    // 예약 생성
    @Override
    @Transactional
    public StayReservationResponseDto createReservation(Long userId, StayReservationRequestDto requestDto) {
        log.info("예약 생성 시작 - userId: {}, accommodationId: {}", userId, requestDto.getAccommodationId());

        // [TODO] GlobalExceptionHandler가 다른 멤버 파일이라 RuntimeException 메시지가 플러터로 전달 안 됨
        // .orElseThrow(() -> new RuntimeException("숙소를 찾을 수 없습니다. id: " + requestDto.getAccommodationId()));
        StayAccommodation accommodation = accommodationRepository.findById(requestDto.getAccommodationId())
                .orElseThrow(() -> new BusinessException(ErrorCode.STAY_ACCOMMODATION_NOT_FOUND, " id: " + requestDto.getAccommodationId()));

        // .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다. id: " + userId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STAY_USER_NOT_FOUND, " id: " + userId));

        // 날짜 중복 예약 체크 (CONFIRMED 상태인 예약만)
        List<StayReservation> confirmed = reservationRepository
                .findByStayAccommodationIdAndStatus(requestDto.getAccommodationId(), StayReservationStatus.CONFIRMED);

        boolean isDuplicate = confirmed.stream().anyMatch(r ->
                !requestDto.getEndDate().isBefore(r.getStartDate()) &&
                        !requestDto.getStartDate().isAfter(r.getEndDate())
        );

        if (isDuplicate) {
            log.info("예약 생성 실패 - 날짜 중복");
            // [TODO] GlobalExceptionHandler가 다른 멤버 파일이라 RuntimeException 메시지가 플러터로 전달 안 됨
            // throw new RuntimeException("선택한 날짜에 이미 예약이 존재합니다.");
            throw new BusinessException(ErrorCode.STAY_RESERVATION_DUPLICATE);
        }

        StayReservation reservation = StayReservation.builder()
                .stayAccommodation(accommodation)
                .user(user)
                .startDate(requestDto.getStartDate())
                .endDate(requestDto.getEndDate())
                .status(StayReservationStatus.CONFIRMED)
                .build();

        StayReservationResponseDto result = StayReservationResponseDto.from(reservationRepository.save(reservation));
        log.info("예약 생성 완료 - reservationId: {}", result.getId());
        return result;
    }

    // 예약 취소 (본인 예약인지 확인 후 CANCELLED 상태로 변경)
    @Override
    @Transactional
    public void cancelReservation(Long reservationId, Long userId) {
        log.info("예약 취소 시작 - reservationId: {}, userId: {}", reservationId, userId);

        // [TODO] GlobalExceptionHandler가 다른 멤버 파일이라 RuntimeException 메시지가 플러터로 전달 안 됨
        // .orElseThrow(() -> new RuntimeException("예약을 찾을 수 없습니다. id: " + reservationId));
        StayReservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STAY_RESERVATION_NOT_FOUND, " id: " + reservationId));

        // 본인 예약인지 확인
        if (!reservation.getUser().getId().equals(userId)) {
            log.info("예약 취소 실패 - 본인 예약 아님");
            // throw new RuntimeException("본인의 예약만 취소할 수 있습니다.");
            throw new BusinessException(ErrorCode.STAY_RESERVATION_UNAUTHORIZED);
        }

        reservation.cancel();
        log.info("예약 취소 완료 - reservationId: {}", reservationId);
    }
}
