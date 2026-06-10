package com.busanit401.spring_back.dto.subscriptionsUser;

import com.busanit401.spring_back.domain.SubscriptionsUser;
import com.busanit401.spring_back.enums.SubscriptionStatus;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class SubscriptionsUserResp {

    private Long subscriptionId;
    private Long userId;
    private String username;
    private Long accommodationId;
    private int durationMonths;
    private LocalDate startDate;
    private LocalDate endDate;
    private SubscriptionStatus status;
    private LocalDateTime createdAt;

    public static SubscriptionsUserResp from(SubscriptionsUser subscriptionsUser) {
        return SubscriptionsUserResp.builder()
                .subscriptionId(subscriptionsUser.getId())
                .userId(subscriptionsUser.getUser().getId())
                .username(subscriptionsUser.getUser().getUsername())
                .accommodationId(subscriptionsUser.getAccommodationId())
                .durationMonths(subscriptionsUser.getDurationMonths())
                .startDate(subscriptionsUser.getStartDate())
                .endDate(subscriptionsUser.getEndDate())
                .status(subscriptionsUser.getStatus())
                .createdAt(subscriptionsUser.getCreatedDate())
                .build();
    }
}
