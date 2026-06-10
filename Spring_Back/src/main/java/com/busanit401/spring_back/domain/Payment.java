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
    private SubscriptionsUser subscriptionsUser;  // subscription → subscriptionsUser

    @Column(name = "payment_key", nullable = false, unique = true)
    private String paymentKey;

    @Column(name = "order_id", nullable = false, unique = true)
    private String orderId;

    @Column(name = "amount", nullable = false)
    private int amount;  // Long → int

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
        if (this.refundedAmount >= this.amount) {
            this.status = PaymentStatus.REFUNDED;
        }
    }
}