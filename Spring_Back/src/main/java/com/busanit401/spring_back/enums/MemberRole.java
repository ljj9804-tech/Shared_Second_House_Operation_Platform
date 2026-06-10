package com.busanit401.spring_back.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberRole {

    LEADER("LEADER_USER"),  // 대표 (결제자)
    MEMBER("MEMBER_USER");   // 멤버

    private final String value;
}
