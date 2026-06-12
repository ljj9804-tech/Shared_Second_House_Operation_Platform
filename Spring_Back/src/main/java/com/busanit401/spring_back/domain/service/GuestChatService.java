package com.busanit401.spring_back.domain.service;

import com.busanit401.spring_back.domain.entity.GuestChatMessage;
import com.busanit401.spring_back.domain.entity.GuestChatRoom;
import com.busanit401.spring_back.domain.repository.GuestChatMessageRepository;
import com.busanit401.spring_back.domain.repository.GuestChatRoomRepository;
import com.busanit401.spring_back.domain.repository.UserRepository; // 🟩 [추정 패키지] 팀원분의 UserRepository 임포트 추가
import com.busanit401.spring_back.domain.User; // 팀원분이 만드신 진짜 엔티티 클래스

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
    private final UserRepository userRepository;

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

    // 채팅방 입장 성공 시 과거 메시지 내역 조회 (Fetch Join 적용된 레포지토리와 매핑 규격 통일)
    public List<GuestChatMessage> getChatHistory(Long chatRoomId) { // 🟩 변수명 chatRoomId로 통일
        log.info("[Service] 과거 대화 내역 디비 조회 시작 - 방 ID: {}", chatRoomId);
        return guestChatMessageRepository.findByGuestChatRoomIdOrderBySentAtAsc(chatRoomId);
    }

    // 실시간으로 수신된 메시지 DB 저장 (유저 엔티티 바인딩 교정)
    @Transactional
    public GuestChatMessage saveMessage(Long chatRoomId, Long senderId, String content) {
        // 1. 채팅방 조회
        GuestChatRoom chatRoom = guestChatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게스트 채팅방입니다. ID: " + chatRoomId));

        User user = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다. ID: " + senderId));

        GuestChatMessage chatMessage = GuestChatMessage.builder()
                .guestChatRoom(chatRoom)
                .sender(user)
                .messageContent(content)
                .build();

        return guestChatMessageRepository.save(chatMessage);
    }
}