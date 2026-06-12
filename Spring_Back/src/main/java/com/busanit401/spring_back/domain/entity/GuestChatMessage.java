package com.busanit401.spring_back.domain.entity;

import com.busanit401.spring_back.domain.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sh_guest_chat_messages")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class GuestChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "guest_chat_message_id")
    private Long id;

    // 대화가 소속된 채팅방
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guest_chat_room_id")
    private GuestChatRoom guestChatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) // user 테이블의 user_id 외래키(FK) 지정
    private User sender;

    // 메시지 내용 (긴 텍스트를 고려해 TEXT 타입 지정)
    @Column(name = "message_content", nullable = false, columnDefinition = "TEXT")
    private String messageContent;

    @Column(name = "sent_at", nullable = false, updatable = false)
    private LocalDateTime sentAt;

    @PrePersist
    protected void onCreate() {
        this.sentAt = LocalDateTime.now();
    }

    // ==============================================================================================
    // ✨ [비즈니스 로직] 실시간 메시지 수정을 위한 도메인 메서드
    // ==============================================================================================
    public void updateContent(String newContent) {
        this.messageContent = newContent;
    }
}