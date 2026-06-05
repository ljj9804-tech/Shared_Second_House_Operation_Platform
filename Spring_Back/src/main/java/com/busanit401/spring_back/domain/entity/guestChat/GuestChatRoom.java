package com.busanit401.spring_back.domain.entity.guestChat;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "guest_chat_rooms")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA용 기본 생성자
@AllArgsConstructor // @Builder 동작을 위한 전체 생성자
@Builder
public class GuestChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "guest_chat_room_id")
    private Long id;

    // 구독한 하우스(상품)의 ID - 이 ID를 기준으로 12명의 게스트가 묶입니다.
    @Column(name = "house_id", nullable = false, unique = true)
    private Long houseId;

    // 채팅방 이름 (예: "A세컨하우스 게스트 단체방")
    @Column(name = "room_name", nullable = false)
    private String roomName;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
