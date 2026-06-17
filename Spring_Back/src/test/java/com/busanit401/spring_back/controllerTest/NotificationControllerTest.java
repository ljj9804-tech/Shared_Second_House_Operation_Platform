package com.busanit401.spring_back.controllerTest;

import com.busanit401.spring_back.dto.notification.NotificationResp;
import com.busanit401.spring_back.enums.NotificationType;
import com.busanit401.spring_back.domain.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@TestPropertySource(properties = {
        "JWT_SECRET=test-secret-key-test-secret-key-test-secret-key"
})
@AutoConfigureMockMvc(addFilters = false)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NotificationService notificationService;

    private NotificationResp mockResponse;

    @BeforeEach
    void setUp() {

        mockResponse = NotificationResp.builder()
                .notificationId(1L)
                .type(NotificationType.SUBSCRIPTION_READY)
                .message("구독 신청이 승인 대기중입니다.")
                .subscriptionId(10L)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
    }

    /**
     * 알림 목록 조회
     */
    @Test
    @DisplayName("알림 목록 조회 성공")
    void getNotifications_success() throws Exception {

        given(notificationService.getMyNotifications(1L))
                .willReturn(List.of(mockResponse));

        mockMvc.perform(
                        get("/api/notifications/1")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].notificationId").value(1L))
                .andExpect(jsonPath("$[0].message")
                        .value("구독 신청이 승인 대기중입니다."))
                .andExpect(jsonPath("$[0].subscriptionId").value(10L))
                .andExpect(jsonPath("$[0].read").value(false));
    }

    /**
     * 읽지 않은 알림 개수
     */
    @Test
    @DisplayName("읽지 않은 알림 개수 조회 성공")
    void getUnreadCount_success() throws Exception {

        given(notificationService.getUnreadCount(1L))
                .willReturn(3L);

        mockMvc.perform(
                        get("/api/notifications/1/unread-count")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("3"));
    }

    /**
     * 알림 읽음 처리
     */
    @Test
    @DisplayName("알림 읽음 처리 성공")
    void read_success() throws Exception {

        willDoNothing()
                .given(notificationService)
                .readNotification(1L);

        mockMvc.perform(
                        put("/api/notifications/1/read")
                )
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    /**
     * 전체 읽음 처리
     */
    @Test
    @DisplayName("전체 읽음 처리 성공")
    void readAll_success() throws Exception {

        willDoNothing()
                .given(notificationService)
                .readAllNotifications(1L);

        mockMvc.perform(
                        put("/api/notifications/1/read-all")
                )
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}
