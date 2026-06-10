package com.busanit401.spring_back.dto.notification;

import com.busanit401.spring_back.domain.Notification;
import com.busanit401.spring_back.enums.NotificationType;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationResp {

    private Long notificationId;
    private NotificationType type;
    private String message;
    private Long subscriptionId;
    private boolean isRead;
    private LocalDateTime createdAt;

    public static NotificationResp from(Notification notification) {
        return NotificationResp.builder()
                .notificationId(notification.getId())
                .type(notification.getType())
                .message(notification.getMessage())
                .subscriptionId(notification.getSubscriptionId())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
