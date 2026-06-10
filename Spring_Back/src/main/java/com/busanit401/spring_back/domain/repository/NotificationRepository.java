package com.busanit401.spring_back.domain.repository;

import com.busanit401.spring_back.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 특정 유저의 전체 알림 조회 (최신순)
    List<Notification> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    // 특정 유저의 읽지 않은 알림 조회
    List<Notification> findAllByUserIdAndIsReadFalse(Long userId);

    // 읽지 않은 알림 개수 (알림 뱃지용)
    long countByUserIdAndIsReadFalse(Long userId);
}
