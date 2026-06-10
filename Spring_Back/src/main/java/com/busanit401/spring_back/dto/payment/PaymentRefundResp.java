package com.busanit401.spring_back.dto.payment;

import com.busanit401.spring_back.domain.PaymentRefund;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.time.YearMonth;

@Getter
@Builder
public class PaymentRefundResp {

    private Long refundId;
    private Long paymentId;
    private YearMonth refundMonth;
    private int refundAmount;
    private String cancelKey;
    private LocalDateTime refundedAt;

    public static PaymentRefundResp from(PaymentRefund refund) {
        return PaymentRefundResp.builder()
                .refundId(refund.getId())
                .paymentId(refund.getPayment().getId())
                .refundMonth(refund.getRefundMonth())
                .refundAmount(refund.getRefundAmount())
                .cancelKey(refund.getCancelKey())
                .refundedAt(refund.getRefundedAt())
                .build();
    }
}