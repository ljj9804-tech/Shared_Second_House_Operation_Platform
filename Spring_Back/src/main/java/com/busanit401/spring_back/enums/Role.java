package com.busanit401.spring_back.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    User("ROLE_USER"),
    SOCIAL("SOCIAL_USER"),
    ADMIN("ADMIN_USER");

    private final String value;


}