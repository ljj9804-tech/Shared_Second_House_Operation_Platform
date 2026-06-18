package com.busanit401.spring_back.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberStatus {
    PENDING("PENDING_USER"),   // 동의 대기
    APPROVED("APPROVED_USER"),  // 동의 완료
    REJECTED("REJECTED_USER");   // 거절

    private final String value;
}