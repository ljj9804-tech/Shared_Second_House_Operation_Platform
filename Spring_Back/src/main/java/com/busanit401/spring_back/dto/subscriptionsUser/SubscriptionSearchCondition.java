package com.busanit401.spring_back.dto.subscriptionsUser;

import com.busanit401.spring_back.enums.SubscriptionStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class SubscriptionSearchCondition {
    private String username;
    private SubscriptionStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
}
