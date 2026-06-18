package com.busanit401.spring_back.domain.entity;

import com.busanit401.spring_back.domain.BaseTimeEntity;
import com.busanit401.spring_back.domain.User;
import com.busanit401.spring_back.enums.StayReservationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "sh_stay_reservation")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class StayReservation extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long id;

    // 어떤 숙소의 예약인지 (FK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accommodation_id", nullable = false)
    private StayAccommodation stayAccommodation;

    // 예약한 유저 (FK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDate startDate;    // 예약 시작일

    @Column(nullable = false)
    private LocalDate endDate;      // 예약 종료일

    // Enum을 DB에 문자열("CONFIRMED", "CANCELLED")로 저장
    // EnumType.ORDINAL(숫자) 대신 STRING 사용 → 가독성 및 안전성 확보
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StayReservationStatus status; // CONFIRMED(예약 확정) / CANCELLED(예약 취소)

    // 예약 취소 비즈니스 메서드 (@Setter 대신 사용)
    public void cancel() {
        this.status = StayReservationStatus.CANCELLED;
    }
}