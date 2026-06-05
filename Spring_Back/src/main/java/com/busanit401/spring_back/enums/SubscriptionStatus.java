package com.busanit401.spring_back.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SubscriptionStatus {
    ACTIVE("ACTIVE_USER"),     // 구독 중
    EXPIRED("EXPIRED_USER"),    // 만료
    CANCELLED("CANCELLED_USER"); // 취소

    private final String value;


}
