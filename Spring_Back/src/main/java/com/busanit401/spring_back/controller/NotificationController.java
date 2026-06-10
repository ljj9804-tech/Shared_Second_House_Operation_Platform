package com.busanit401.spring_back.controller;

import com.busanit401.spring_back.dto.notification.NotificationResp;
import com.busanit401.spring_back.domain.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // 알림 목록 조회
    @GetMapping("/{userId}")
    public ResponseEntity<List<NotificationResp>> getNotifications(
            @PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getMyNotifications(userId));
    }

    // 읽지 않은 알림 개수 (뱃지용)
    @GetMapping("/{userId}/unread-count")
    public ResponseEntity<Long> getUnreadCount(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getUnreadCount(userId));
    }

    // 알림 읽음 처리
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Void> read(@PathVariable Long notificationId) {
        notificationService.readNotification(notificationId);
        return ResponseEntity.noContent().build();
    }

    // 전체 읽음 처리
    @PutMapping("/{userId}/read-all")
    public ResponseEntity<Void> readAll(@PathVariable Long userId) {
        notificationService.readAllNotifications(userId);
        return ResponseEntity.noContent().build();
    }
}