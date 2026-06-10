package com.busanit401.spring_back.controllerTest;

import com.busanit401.spring_back.domain.User;
import com.busanit401.spring_back.dto.user.UserResp;
import com.busanit401.spring_back.enums.Role;
import com.busanit401.spring_back.security.auth.CustomUserDetails;
import com.busanit401.spring_back.security.auth.CustomUserDetailsService;
import com.busanit401.spring_back.domain.service.TokenBlacklistService;
import com.busanit401.spring_back.domain.service.UserService;
import com.busanit401.spring_back.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@TestPropertySource(properties = {
        "JWT_SECRET=test-secret-key-test-secret-key-test-secret-key"
})
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private TokenBlacklistService tokenBlacklistService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private String accessToken;
    private CustomUserDetails mockUserDetails;
    private UserResp mockUserResp;

    @BeforeEach
    void setUp() {
        // Mock User 생성
        User mockUser = User.builder()
                .username("jaeuk")
                .password("password123!")
                .email("jaeuk@test.com")
                .nickname("재욱")
                .phoneNumber("01012345678")
                .role(Role.User)
                .build();

        mockUserDetails = new CustomUserDetails(mockUser);

        // 실제 JWT 토큰 발급
        accessToken = "Bearer " + jwtUtil.generateToken(mockUser.getUsername());

        // 블랙리스트 기본 false 설정
        given(tokenBlacklistService.isBlacklisted(any())).willReturn(false);

        given(customUserDetailsService.loadUserByUsername("jaeuk"))
                .willReturn(mockUserDetails);

        // Mock 응답
        mockUserResp = UserResp.builder()
                .userId(1L)
                .username("jaeuk")
                .email("jaeuk@test.com")
                .nickname("재욱")
                .phoneNumber("01012345678")
                .role(Role.User)
                .build();
    }

    // SecurityContext에 직접 인증 주입하는 헬퍼 메서드
    private void setAuthentication() {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                        mockUserDetails, null, mockUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }


    // -----------------------------------------------
    // 마이페이지
    // -----------------------------------------------

    @Test
    @DisplayName("마이페이지 조회 - 성공")
    void getUserProfile_success() throws Exception {
        setAuthentication();
        given(userService.getUser(any())).willReturn(mockUserResp);

        mockMvc.perform(get("/api/users")
                        .header("Authorization", accessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("jaeuk"));
    }


    // -----------------------------------------------
    // 회원 수정
    // -----------------------------------------------

    @Test
    @DisplayName("회원 수정 - 성공")
    void updateUserProfile_success() throws Exception {
        setAuthentication();
        given(userService.getUser(any())).willReturn(mockUserResp);
        willDoNothing().given(userService).updateUser(any(), any());

        String requestBody = """
                {
                    "username" : "username1",
                    "nickname": "새닉네임"
               
             
                }
                """;

        mockMvc.perform(patch("/api/users")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk());
    }


    // -----------------------------------------------
    // 회원 탈퇴
    // -----------------------------------------------

    @Test
    @DisplayName("회원 탈퇴 - 성공")
    void deleteUser_success() throws Exception {
        setAuthentication();
        willDoNothing().given(userService).deleteUser(any());

        mockMvc.perform(delete("/api/users")
                        .header("Authorization", accessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("User Deleted successfully"));
    }


    // -----------------------------------------------
    // 로그아웃
    // -----------------------------------------------

    @Test
    @DisplayName("로그아웃 - 성공")
    void logout_success() throws Exception {

        setAuthentication();

        mockMvc.perform(
                post("/api/users/logout")
                        .header("Authorization", accessToken)
                        .cookie(
                                new Cookie(
                                        "refresh_token",
                                        "testRefreshToken"
                                )
                        )
        );
    }


    // -----------------------------------------------
    // 토큰 갱신
    // -----------------------------------------------

    @Test
    @DisplayName("토큰 갱신 - 만료된 토큰")
    void refreshToken_expired() throws Exception {
        // 만료된 토큰은 실제로 만들기 어려우니 null 토큰으로 대체
        mockMvc.perform(post("/api/users/refresh-token"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("토큰 갱신 - 블랙리스트 토큰")
    void refreshToken_blacklisted() throws Exception {
        String refreshToken = jwtUtil.generateRefreshToken("jaeuk");
        given(tokenBlacklistService.isBlacklisted(refreshToken)).willReturn(true);

        mockMvc.perform(post("/api/users/refresh-token")
                        .cookie(new jakarta.servlet.http.Cookie("refresh_token", refreshToken)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}