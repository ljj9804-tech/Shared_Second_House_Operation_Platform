package com.busanit401.spring_back.dto.waitingSubscriptionUser;

import com.busanit401.spring_back.enums.MemberStatus;
import com.busanit401.spring_back.enums.MemberRole;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WaitingSubscriptionSearchCondition {
    private String username;
    private MemberStatus status;
    private MemberRole memberRole;
}
