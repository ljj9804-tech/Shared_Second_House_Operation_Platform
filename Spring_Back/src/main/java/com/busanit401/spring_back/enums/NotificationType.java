package com.busanit401.spring_back.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {

    SUBSCRIPTION_READY("구독 승인 요청"),   // 멤버 전원 동의 완료 → 관리자
    SUBSCRIPTION_APPROVED("구독 승인"),     // 관리자 승인 → 유저
    SUBSCRIPTION_REJECTED("구독 반려"),     // 관리자 반려 → 유저
    SUBSCRIPTION_CANCELLED("구독 취소"),    // 구독 취소 → 관리자
    MEMBER_INVITED("구독 초대");            // 멤버 초대 → 멤버

    private final String value;
}
