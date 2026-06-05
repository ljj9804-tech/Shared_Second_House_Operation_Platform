package com.busanit401.spring_back.domain.service.guestChat;

import com.busanit401.spring_back.domain.entity.guestChat.GuestChatMessage;
import com.busanit401.spring_back.domain.entity.guestChat.GuestChatRoom;
import com.busanit401.spring_back.domain.repository.guestChat.GuestChatMessageRepository;
import com.busanit401.spring_back.domain.repository.guestChat.GuestChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GuestChatService {

    private final GuestChatRoomRepository guestChatRoomRepository;
    private final GuestChatMessageRepository guestChatMessageRepository;

    // 상품 구독 완료 시 채팅방 자동 생성
    // 채팅방 이름 "{houseId}번 하우스 채팅방"으로 자동 설정됨.
    @Transactional
    public GuestChatRoom createChatRoom(Long houseId) {
        return guestChatRoomRepository.findByHouseId(houseId)
                .orElseGet(() -> {
                    GuestChatRoom newRoom = GuestChatRoom.builder()
                            .houseId(houseId)
                            .roomName(houseId + "번 하우스 채팅방") //자동 이름 설정
                            .build();
                    return guestChatRoomRepository.save(newRoom);
                });
    }

    // 게스트가 채팅방 입장 시점 호출: 단순히 기존 방을 조회
    public GuestChatRoom getChatRoom(Long houseId) {
        return guestChatRoomRepository.findByHouseId(houseId)
                .orElseThrow(() -> {
                    // ⚠️채팅방 조회 실패 시 확인용 콘솔로그⚠️
                    log.info("[⚠️ 게스트 채팅] 채팅방이 존재하지 않음 - 요청된 하우스 ID: {}", houseId);
                    return new IllegalArgumentException("해당 하우스의 채팅방이 존재하지 않습니다. 하우스 ID: " + houseId);
                });
    }

    // 채팅방 입장 성공 시 과거 메시지 내역 조회 (기존 유지)
    public List<GuestChatMessage> getChatHistory(Long guestChatRoomId) {
        return guestChatMessageRepository.findByGuestChatRoomIdOrderBySentAtAsc(guestChatRoomId);
    }

    // 실시간으로 수신된 메시지 DB 저장 (기존 유지)
    @Transactional
    public GuestChatMessage saveMessage(Long chatRoomId, Long senderId, String senderName, String content) {
        GuestChatRoom chatRoom = guestChatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게스트 채팅방입니다. ID: " + chatRoomId));

        GuestChatMessage chatMessage = GuestChatMessage.builder()
                .guestChatRoom(chatRoom)
                .senderId(senderId)
                .senderName(senderName)
                .messageContent(content)
                .build();

        return guestChatMessageRepository.save(chatMessage);
    }
}