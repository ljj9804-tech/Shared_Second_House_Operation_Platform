package com.busanit401.spring_back.dto.PaymentRefund;

import lombok.Builder;
import lombok.Getter;
import java.time.YearMonth;

@Getter
@Builder
public class PaymentRefundSearchCondition {
    private String username;
    private YearMonth refundMonth;
}
