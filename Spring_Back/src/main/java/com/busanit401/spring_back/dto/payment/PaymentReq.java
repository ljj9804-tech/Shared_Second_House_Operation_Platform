package com.busanit401.spring_back.dto.payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentReq {

    @NotBlank(message = "paymentKey를 입력해주세요.")
    private String paymentKey;

    @NotBlank(message = "orderId를 입력해주세요.")
    private String orderId;

    @NotNull(message = "amount를 입력해주세요.")
    private int amount;

    @NotNull(message = "구독 ID를 입력해주세요.")
    private Long subscriptionsUserId;
}