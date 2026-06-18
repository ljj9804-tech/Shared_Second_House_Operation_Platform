package com.busanit401.spring_back.dto.payment;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.YearMonth;

@Getter
@NoArgsConstructor
public class PaymentRefundReq {

    @NotNull(message = "환불할 월을 입력해주세요.")
    private YearMonth refundMonth;

    @NotNull(message = "환불 금액을 입력해주세요.")
    private int refundAmount;
}