package com.busanit401.spring_back.domain.entity.guestChat;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "guest_chat_messages")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class GuestChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "guest_chat_message_id")
    private Long id;

    // 대화가 소속된 채팅방 (Many-to-One 단방향 연관관계 추천)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guest_chat_room_id", nullable = false)
    private GuestChatRoom guestChatRoom;

    // 메시지를 보낸 게스트(사용자)의 ID 혹은 이름
    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    @Column(name = "sender_name", nullable = false)
    private String senderName;

    // 메시지 내용 (긴 텍스트를 고려해 굵은 내용도 커버 가능하도록 TEXT 타입 지정)
    @Column(name = "message_content", nullable = false, columnDefinition = "TEXT")
    private String messageContent;

    @Column(name = "sent_at", nullable = false, updatable = false)
    private LocalDateTime sentAt;

    @PrePersist
    protected void onCreate() {
        this.sentAt = LocalDateTime.now();
    }
}