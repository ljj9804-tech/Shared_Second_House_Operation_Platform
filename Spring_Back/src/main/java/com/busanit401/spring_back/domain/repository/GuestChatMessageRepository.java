package com.busanit401.spring_back.domain.repository;

import com.busanit401.spring_back.domain.entity.GuestChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GuestChatMessageRepository extends JpaRepository<GuestChatMessage, Long> {
    @Query("select m from GuestChatMessage m " +
            "join fetch m.sender " +
            "where m.guestChatRoom.id = :chatRoomId " +
            "order by m.sentAt asc")
    List<GuestChatMessage> findByGuestChatRoomIdOrderBySentAtAsc(@Param("chatRoomId") Long chatRoomId);
}
