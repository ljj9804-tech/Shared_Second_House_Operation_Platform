package com.busanit401.spring_back.controllerTest;


import com.busanit401.spring_back.dto.subscriptionsUser.SubscriptionsUserResp;
import com.busanit401.spring_back.enums.SubscriptionStatus;
import com.busanit401.spring_back.domain.service.SubscriptionsUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@TestPropertySource(properties = {
        "JWT_SECRET=test-secret-key-test-secret-key-test-secret-key"
})
@AutoConfigureMockMvc(addFilters = false)
class SubscriptionsUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SubscriptionsUserService subscriptionsUserService;

    private SubscriptionsUserResp mockResponse;

    @BeforeEach
    void setUp() {

        mockResponse = SubscriptionsUserResp.builder()
                .subscriptionId(1L)
                .userId(1L)
                .username("leader")
                .accommodationId(100L)
                .durationMonths(12)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(12))
                .status(SubscriptionStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();
    }

    /**
     * 내 구독 목록 조회
     */
    @Test
    @DisplayName("내 구독 목록 조회 성공")
    void getMySubscriptions_success() throws Exception {

        given(subscriptionsUserService.getMySubscriptions(1L))
                .willReturn(List.of(mockResponse));

        mockMvc.perform(
                        get("/api/subscriptions/my/1")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].subscriptionId").value(1L))
                .andExpect(jsonPath("$[0].username").value("leader"));
    }

    /**
     * 구독 상세 조회
     */
    @Test
    @DisplayName("구독 상세 조회 성공")
    void getSubscription_success() throws Exception {

        given(subscriptionsUserService.getSubscription(1L))
                .willReturn(mockResponse);

        mockMvc.perform(
                        get("/api/subscriptions/1")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subscriptionId").value(1L));    }

    /**
     * 구독 취소
     */
    @Test
    @DisplayName("구독 취소 성공")
    void cancel_success() throws Exception {

        given(subscriptionsUserService.cancel(1L, 1L))
                .willReturn(mockResponse);

        mockMvc.perform(
                        delete("/api/subscriptions/1/1")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subscriptionId").value(1L));    }

    /**
     * 관리자 승인
     */
    @Test
    @DisplayName("구독 승인 성공")
    void approve_success() throws Exception {

        given(subscriptionsUserService.approve(1L))
                .willReturn(mockResponse);

        mockMvc.perform(
                        put("/api/subscriptions/admin/1/approve")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    /**
     * 관리자 반려
     */
    @Test
    @DisplayName("구독 반려 성공")
    void reject_success() throws Exception {

        SubscriptionsUserResp rejected = SubscriptionsUserResp.builder()
                .subscriptionId(1L)
                .userId(1L)
                .username("leader")
                .status(SubscriptionStatus.EXPIRED)
                .build();

        given(subscriptionsUserService.reject(1L))
                .willReturn(rejected);

        mockMvc.perform(
                        put("/api/subscriptions/admin/1/reject")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("EXPIRED"));
    }

    /**
     * 관리자 검색
     */
    @Test
    @DisplayName("구독 검색 성공")
    void search_success() throws Exception {

        given(subscriptionsUserService.searchByCondition(any()))
                .willReturn(List.of(mockResponse));

        mockMvc.perform(
                        get("/api/subscriptions/admin/search")
                                .param("username", "leader")
                                .param("status", "ACTIVE")
                                .param("startDate", "2026-01-01")
                                .param("endDate", "2026-12-31")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].subscriptionId").value(1L))
                .andExpect(jsonPath("$[0].username").value("leader"))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));
    }
}
