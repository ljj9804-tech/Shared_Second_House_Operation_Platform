package com.busanit401.spring_back.domain.repository;

import com.busanit401.spring_back.domain.entity.GuestChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GuestChatMessageRepository extends JpaRepository<GuestChatMessage, Long> {
    // 특정 채팅방의 과거 내역을 시간순으로 조회
    List<GuestChatMessage> findByGuestChatRoomIdOrderBySentAtAsc(Long guestChatRoomId);
}
