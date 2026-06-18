package com.busanit401.spring_back.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentStatus {

    PENDING("PENDING_USER"),    // 결제 대기
    COMPLETED("COMPLETED_USER"),  // 결제 완료
    FAILED("FAILED_USER"),     // 결제 실패
    REFUNDED("REFUNDED_USER");   // 환불

    private final String value;
}