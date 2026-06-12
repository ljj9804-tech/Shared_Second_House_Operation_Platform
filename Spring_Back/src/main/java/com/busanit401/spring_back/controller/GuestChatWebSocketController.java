package com.busanit401.spring_back.controller;

import com.busanit401.spring_back.domain.service.GuestChatService;
import com.busanit401.spring_back.dto.GuestChatDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;


@Log4j2
@Controller
@RequiredArgsConstructor
public class GuestChatWebSocketController {

    private final GuestChatService guestChatService;
    private final SimpMessagingTemplate messagingTemplate; // 스프링이 제공하는 실시간 메시지 발송기

    @MessageMapping("/guest/chat/send") // 실제 플러터 발신 주소: /app/guest/chat/send
    public void broadcastMessage(GuestChatDto chatDto) {
        log.info("[🚀 실시간 웹소켓] 메시지 도착 - 방: {}, 보낸이: {}, 내용: {}",
                chatDto.getChatRoomId(), chatDto.getSenderName(), chatDto.getContent());

        // [행동 1] 수신된 대화 내용을 DB에 영구 저장 (서비스 호출)
        guestChatService.saveMessage(
                chatDto.getChatRoomId(),
                chatDto.getSenderId(),
                chatDto.getContent()
        );

        // [행동 2] 대화가 일어난 특정 방의 주소 조립
        // 예시 주소: /topic/guest/room/1
        String destination = "/topic/guest/room/" + chatDto.getChatRoomId();

        // [행동 3] 그 방에 모여있는 모든 사용자들에게 DTO 배달 가방을 실시간 PUSH!
        messagingTemplate.convertAndSend(destination, chatDto);

        log.info("[📢 브로드캐스팅 완료] 목적지: {} -> 실시간 전송 성공", destination);
    }
}

/*
` * ============================================================================
 * [컨트롤러] 실시간 웹소켓(STOMP) 메시지 중재자 (GuestChatWebSocketController)
 * * 📌 주요 역할 및 흐름 요약
 * 1. 클라이언트 발신 접수 (@MessageMapping):
 * - Flutter 앱이 "/app/guest/chat/send" 주소로 던진 메시지를 DTO 바구니로 받음.
 * 2. 서비스 로직 연동:
 * - 수신된 메시지를 비즈니스 서비스에 넘겨 마리아DB에 실시간 영구 저장.
 * 3. 실시간 브로드캐스팅 (convertAndSend):
 * - 해당 방을 구독 중인 모든 사용자들(/topic/guest/room/{방번호})에게 대화 내용을 즉시 밀어내기(Push).
 * =====`=======================================================================
 */