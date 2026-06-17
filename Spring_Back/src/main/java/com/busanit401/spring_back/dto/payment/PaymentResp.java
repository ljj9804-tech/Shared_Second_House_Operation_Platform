package com.busanit401.spring_back.dto.payment;

import com.busanit401.spring_back.domain.Payment;
import com.busanit401.spring_back.enums.PaymentStatus;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class PaymentResp {

    private Long paymentId;
    private Long subscriptionsUserId;
    private String paymentKey;
    private String orderId;
    private int amount;
    private int refundedAmount;
    private PaymentStatus status;
    private LocalDateTime paidAt;

    public static PaymentResp from(Payment payment) {
        return PaymentResp.builder()
                .paymentId(payment.getId())
                .subscriptionsUserId(payment.getSubscriptionsUser().getId())
                .paymentKey(payment.getPaymentKey())
                .orderId(payment.getOrderId())
                .amount(payment.getAmount())
                .refundedAmount(payment.getRefundedAmount())
                .status(payment.getStatus())
                .paidAt(payment.getPaidAt())
                .build();
    }
}