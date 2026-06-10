package com.busanit401.spring_back.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.time.YearMonth;

@Entity
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Table(name = "payment_refund")
public class PaymentRefund {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refund_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    // 환불 대상 월 (예: 2025-03)
    @Column(name = "refund_month", nullable = false)
    private YearMonth refundMonth;

    // 해당 월 환불 금액
    @Column(name = "refund_amount", nullable = false)
    private int refundAmount;

    // 토스페이먼츠 환불 처리 후 받는 취소 키
    @Column(name = "cancel_key")
    private String cancelKey;

    @Column(name = "refunded_at", nullable = false)
    private LocalDateTime refundedAt;
}