package com.busanit401.spring_back.domain;

import com.busanit401.spring_back.enums.NotificationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Table(name = "notification")
public class Notification {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    // 알림 받을 유저 (관리자 또는 일반 유저)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private NotificationType type;

    @Column(name = "message", nullable = false)
    private String message;

    // 관련 구독 ID (클릭 시 해당 구독으로 이동)
    @Column(name = "subscription_id")
    private Long subscriptionId;

    @Column(name = "is_read", nullable = false)
    @Builder.Default
    private boolean isRead = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;


    // 읽음 처리
    public void read() {
        this.isRead = true;
    }

    // 정적 팩토리 메서드
    public static Notification create(User user, NotificationType type,
                                      String message, Long subscriptionId) {
        return Notification.builder()
                .user(user)
                .type(type)
                .message(message)
                .subscriptionId(subscriptionId)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
