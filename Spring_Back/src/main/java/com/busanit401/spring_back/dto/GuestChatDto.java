package com.busanit401.spring_back.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class GuestChatDto {

    // 1. 대화가 오가는 채팅방의 고유 ID (어느 방으로 보낼지 구별)
    private Long chatRoomId;

    // 2. 메시지를 보낸 게스트의 회원 고유 ID
    private Long senderId;

    // 3. 채팅창에 표시될 보낸 사람의 이름 또는 닉네임
    private String senderName;

    // 4. 실시간으로 전송할 대화 내용 본문
    private String content;
}


/*
 * ============================================================================
 * [DTO] 실시간 게스트 채팅 데이터 전송용 가방 (GuestChatDto)
 * * 📌 주요 역할 및 특징
 * 1. 실시간 메시지 발신(/app) 및 수신(/topic) 시 데이터를 담는 가벼운 바구니 역할
 * 2. 엔티티(Entity)를 직접 노출하지 않고, 채팅에 딱 필요한 최소한의 데이터만 포함하여 보안 및 성능 최적화
 * ============================================================================
 */