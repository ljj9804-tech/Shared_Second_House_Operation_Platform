package com.busanit401.spring_back.controller;

import com.busanit401.spring_back.domain.entity.GuestChatMessage;
import com.busanit401.spring_back.domain.entity.GuestChatRoom;
import com.busanit401.spring_back.domain.service.GuestChatService;
import com.busanit401.spring_back.dto.GuestChatDto;
import lombok.RequiredArgsConstructor;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RestController
@RequestMapping("/api/guest/chat")
@RequiredArgsConstructor
public class GuestChatController {

    private final GuestChatService guestChatService;

    /**
     * 1. 특정 하우스의 채팅방 정보 및 ID 조회 API
     * GET http://localhost:8080/api/guest/chat/room/{houseId}
     */
    @GetMapping("/room/{houseId}")
    public ResponseEntity<GuestChatRoom> getRoomInfo(@PathVariable("houseId") Long houseId) {
        log.info("[API GET] 채팅방 정보 조회 요청 - 하우스 ID: {}", houseId);
        GuestChatRoom chatRoom = guestChatService.getChatRoom(houseId);
        return ResponseEntity.ok(chatRoom);
    }

    /**
     * 2. 특정 채팅방의 과거 메시지 내역 조회 API (Flutter 화면 진입 시 호출)
     * GET http://localhost:8080/api/guest/chat/history/{chatRoomId}
     */
    @GetMapping("/history/{chatRoomId}")
    public ResponseEntity<List<GuestChatDto>> getChatHistory(@PathVariable("chatRoomId") Long chatRoomId) {
        log.info("[API GET] 과거 대화 내역 조회 요청 - 채팅방 ID: {}", chatRoomId);

        List<GuestChatMessage> history = guestChatService.getChatHistory(chatRoomId);

        List<GuestChatDto> dtoList = history.stream().map(msg -> GuestChatDto.builder()
                .type("TALK")
                .chatId(msg.getId())
                .chatRoomId(chatRoomId)
                .senderId(msg.getSender().getId())
                .senderName(msg.getSender().getNickname())
                .content(msg.getMessageContent())
                .build()
        ).toList();

        return ResponseEntity.ok(dtoList);
    }
}