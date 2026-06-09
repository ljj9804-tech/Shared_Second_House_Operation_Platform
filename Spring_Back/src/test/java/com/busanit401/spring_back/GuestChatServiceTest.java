package com.busanit401.spring_back;


import com.busanit401.spring_back.domain.entity.guestChat.GuestChatMessage;
import com.busanit401.spring_back.domain.entity.guestChat.GuestChatRoom;
import com.busanit401.spring_back.domain.repository.guestChat.GuestChatMessageRepository;
import com.busanit401.spring_back.domain.repository.guestChat.GuestChatRoomRepository;
import com.busanit401.spring_back.domain.service.guestChat.GuestChatService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Log4j2
@ExtendWith(MockitoExtension.class) // 가짜 객체(Mock)를 사용하기 위한 설정
class GuestChatServiceTest {

    @Mock
    private GuestChatRoomRepository guestChatRoomRepository;

    @Mock
    private GuestChatMessageRepository guestChatMessageRepository;

    @InjectMocks
    private GuestChatService guestChatService; // 위의 가짜 레포지토리들이 주입된 서비스 객체

    @Test
    @DisplayName("구독 시 채팅방 자동 생성 테스트 - 기존 방이 없을 때")
    void createChatRoomForSubscribedHouse_Success() {
        // given (준비)
        Long houseId = 100L;
        GuestChatRoom savedRoom = GuestChatRoom.builder()
                .id(1L)
                .houseId(houseId)
                .roomName("100번 하우스 채팅방")
                .build();

        // 레포지토리가 검색했을 때 없다고 가정(Optional.empty)하고, 저장할 때는 savedRoom을 준다고 약속
        given(guestChatRoomRepository.findByHouseId(houseId)).willReturn(Optional.empty());
        given(guestChatRoomRepository.save(any(GuestChatRoom.class))).willReturn(savedRoom);

        // when (실행)
        GuestChatRoom result = guestChatService.createChatRoom(houseId);

        // then (검증)
        assertThat(result).isNotNull();
        assertThat(result.getRoomName()).isEqualTo("100번 하우스 채팅방");
        assertThat(result.getHouseId()).isEqualTo(houseId);

        log.info("🟩 채팅방 생성 테스트 완료: {}", result.getRoomName());
    }

    @Test
    @DisplayName("채팅방 입장 조회 실패 시 로그 출력 및 예외 발생 테스트")
    void getChatRoom_Fail_ThrowException() {
        // given
        Long houseId = 999L;
        // 999번 하우스로 조회하면 결과가 없다고 가짜 설정
        given(guestChatRoomRepository.findByHouseId(houseId)).willReturn(Optional.empty());

        // when & then (실행 및 에러 검증)
        // 서비스를 호출했을 때 IllegalArgumentException 에러가 터지는지 확인
        assertThatThrownBy(() -> guestChatService.getChatRoom(houseId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("해당 하우스의 채팅방이 존재하지 않습니다.");

        log.info("🟩 예외 및 콘솔 로그 출력 테스트 완료");
    }

    @Test
    @DisplayName("과거 메시지 내역 조회 테스트")
    void getChatHistory_Success() {
        // given
        Long chatRoomId = 1L;
        GuestChatMessage msg1 = GuestChatMessage.builder().id(1L).messageContent("안녕하세요").build();
        GuestChatMessage msg2 = GuestChatMessage.builder().id(2L).messageContent("반갑습니다").build();

        given(guestChatMessageRepository.findByGuestChatRoomIdOrderBySentAtAsc(chatRoomId))
                .willReturn(List.of(msg1, msg2));

        // when
        List<GuestChatMessage> history = guestChatService.getChatHistory(chatRoomId);

        // then
        assertThat(history).hasSize(2);
        assertThat(history.get(0).getMessageContent()).isEqualTo("안녕하세요");

        log.info("🟩 과거 내역 조회 테스트 완료: 가져온 메시지 개수 {}개", history.size());
    }
}
