package com.busanit401.spring_back.domain.service;

import com.busanit401.spring_back.domain.Notification;
import com.busanit401.spring_back.domain.User;
import com.busanit401.spring_back.dto.notification.NotificationResp;
import com.busanit401.spring_back.enums.NotificationType;
import com.busanit401.spring_back.enums.Role;
import com.busanit401.spring_back.exception.EntityNotFoundException;
import com.busanit401.spring_back.exception.ErrorCode;
import com.busanit401.spring_back.domain.repository.NotificationRepository;
import com.busanit401.spring_back.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;


    // 관리자한테 알림 전송
    @Transactional
    public void notifyAdmin(NotificationType type, String message, Long subscriptionId) {
        // ADMIN Role을 가진 유저 전체 조회
        List<User> admins = userRepository.findAllByRole(Role.ADMIN);
        List<Notification> notifications = admins.stream()
                .map(admin -> Notification.create(admin, type, message, subscriptionId))
                .collect(Collectors.toList());
        notificationRepository.saveAll(notifications);
        log.info("[관리자 알림 전송] type: {}, 관리자 수: {}", type, admins.size());
    }


    // 특정 유저한테 알림 전송
    @Transactional
    public void notifyUser(Long userId, NotificationType type, String message, Long subscriptionId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));
        notificationRepository.save(Notification.create(user, type, message, subscriptionId));
        log.info("[유저 알림 전송] userId: {}, type: {}", userId, type);
    }


    // 내 알림 목록 조회
    public List<NotificationResp> getMyNotifications(Long userId) {
        return notificationRepository.findAllByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(NotificationResp::from)
                .collect(Collectors.toList());
    }


    // 읽지 않은 알림 개수 (뱃지용)
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }


    // 알림 읽음 처리
    @Transactional
    public void readNotification(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));
        notification.read();
    }


    // 전체 알림 읽음 처리
    @Transactional
    public void readAllNotifications(Long userId) {
        List<Notification> unread = notificationRepository.findAllByUserIdAndIsReadFalse(userId);
        unread.forEach(Notification::read);
    }
}