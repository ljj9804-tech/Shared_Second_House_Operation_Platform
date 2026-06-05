package com.busanit401.spring_back.domain;

import com.busanit401.spring_back.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Table(name = "payment")
public class Payment extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id", nullable = false)
    private SubscriptionsUser subscription;

    // 토스페이먼츠 결제 고유키 (환불 시 필수)
    @Column(name = "payment_key", nullable = false, unique = true)
    private String paymentKey;

    // 토스페이먼츠 주문 ID
    @Column(name = "order_id", nullable = false, unique = true)
    private String orderId;

    // 전체 결제 금액
    @Column(name = "amount", nullable = false)
    private Long amount;

    // 환불된 총 금액 (월별 환불 누적)
    @Column(name = "refunded_amount", nullable = false)
    @Builder.Default
    private int refundedAmount = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<PaymentRefund> refunds = new ArrayList<>();


    // 결제 완료
    public void complete(String paymentKey, String orderId) {
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.status = PaymentStatus.COMPLETED;
        this.paidAt = LocalDateTime.now();
    }

    // 결제 실패
    public void fail() {
        this.status = PaymentStatus.FAILED;
    }

    // 월별 부분 환불
    public void refundMonth(int refundAmount) {
        this.refundedAmount += refundAmount;
        // 전액 환불된 경우 상태 변경
        if (this.refundedAmount >= this.amount) {
            this.status = PaymentStatus.REFUNDED;
        }
    }
}