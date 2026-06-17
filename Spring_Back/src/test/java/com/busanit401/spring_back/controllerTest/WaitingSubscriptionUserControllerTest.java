package com.busanit401.spring_back.controllerTest;

import com.busanit401.spring_back.dto.waitingSubscriptionUser.WaitingSubscriptionResp;
import com.busanit401.spring_back.enums.MemberRole;
import com.busanit401.spring_back.enums.MemberStatus;
import com.busanit401.spring_back.domain.service.WaitingSubscriptionUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@TestPropertySource(properties = {
        "JWT_SECRET=test-secret-key-test-secret-key-test-secret-key"
})
@AutoConfigureMockMvc(addFilters = false)
class WaitingSubscriptionUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private WaitingSubscriptionUserService waitingService;

    private WaitingSubscriptionResp mockResponse;

    @BeforeEach
    void setUp() {

        mockResponse = WaitingSubscriptionResp.builder()
                .waitingId(1L)
                .subscriptionId(10L)
                .userId(1L)
                .username("member1")
                .memberRole(MemberRole.MEMBER)
                .status(MemberStatus.PENDING)
                .requestedAt(LocalDateTime.now())
                .build();
    }

    /**
     * 구독 신청
     */
    @Test
    @DisplayName("구독 신청 성공")
    void apply_success() throws Exception {

        given(waitingService.apply(any(), any()))
                .willReturn(List.of(mockResponse));

        String requestBody = """
            {
                "accommodationId": 100,
                "durationMonths": 12,
                "memberIdentifiers": [
                    "member1",
                    "member2"
                ]
            }
            """;

        mockMvc.perform(
                        post("/api/waiting/apply/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].waitingId").value(1L))
                .andExpect(jsonPath("$[0].subscriptionId").value(10L));
    }

    /**
     * 멤버 승인
     */
    @Test
    @DisplayName("멤버 승인 성공")
    void approve_success() throws Exception {

        given(waitingService.approveMember(1L, 1L))
                .willReturn(mockResponse);

        mockMvc.perform(
                        post("/api/waiting/1/approve/1")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.waitingId").value(1L))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    /**
     * 멤버 거절
     */
    @Test
    @DisplayName("멤버 거절 성공")
    void reject_success() throws Exception {

        WaitingSubscriptionResp rejected =
                WaitingSubscriptionResp.builder()
                        .waitingId(1L)
                        .subscriptionId(10L)
                        .userId(1L)
                        .username("member1")
                        .memberRole(MemberRole.MEMBER)
                        .status(MemberStatus.REJECTED)
                        .build();

        given(waitingService.rejectMember(1L, 1L))
                .willReturn(rejected);

        mockMvc.perform(
                        post("/api/waiting/1/reject/1")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"));
    }

    /**
     * 내 초대 목록 조회
     */
    @Test
    @DisplayName("내 초대 목록 조회 성공")
    void getMyInvitations_success() throws Exception {

        given(waitingService.getMyInvitations(1L))
                .willReturn(List.of(mockResponse));

        mockMvc.perform(
                        get("/api/waiting/my/1")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].waitingId").value(1L))
                .andExpect(jsonPath("$[0].username").value("member1"));
    }

    /**
     * 관리자 검색
     */
    @Test
    @DisplayName("초대 검색 성공")
    void search_success() throws Exception {

        given(waitingService.searchByCondition(any()))
                .willReturn(List.of(mockResponse));

        mockMvc.perform(
                        get("/api/waiting/admin/search")
                                .param("username", "member1")
                                .param("status", "PENDING")
                                .param("startDate", "2026-01-01")
                                .param("endDate", "2026-12-31")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].waitingId").value(1L))
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }
}
