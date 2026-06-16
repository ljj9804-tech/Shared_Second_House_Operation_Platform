package com.busanit401.spring_back.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GuestChatDto {
    private String type;         // TALK, EDIT, DELETE
    private Long chatId;         // 메시지 고유 번호 (수정/삭제의 열쇠)
    private Long chatRoomId;     // 방 번호
    private Long senderId;       // 보낸 사람 User ID
    private String senderName;   // 보낸 사람 닉네임 (화면 표시용)
    private String content;      // 메시지 내용
}

/*
 * ============================================================================
 * [DTO] 실시간 게스트 채팅 데이터 전송용 가방 (GuestChatDto)
 * * 📌 주요 역할 및 특징
 * 1. 실시간 메시지 발신(/app) 및 수신(/topic) 시 데이터를 담는 가벼운 바구니 역할
 * 2. 엔티티(Entity)를 직접 노출하지 않고, 채팅에 딱 필요한 최소한의 데이터만 포함하여 보안 및 성능 최적화
 * ============================================================================
 */