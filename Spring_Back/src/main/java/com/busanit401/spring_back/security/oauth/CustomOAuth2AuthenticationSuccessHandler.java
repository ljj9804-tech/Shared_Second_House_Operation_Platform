package com.busanit401.spring_back.security.oauth;

import com.busanit401.spring_back.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;


@RequiredArgsConstructor
@Component
public class CustomOAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        String accessToken = jwtUtil.generateToken(authentication.getName());
        String refreshToken = jwtUtil.generateRefreshToken(authentication.getName());

        // refreshToken은 HttpOnly 쿠키에 담기
        Cookie refreshTokenCookie = new Cookie("refresh_token", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(Math.toIntExact(jwtUtil.getRefreshExpiration() / 1000));
        response.addCookie(refreshTokenCookie);

        // accessToken은 URL 파라미터에 담아서 프론트 콜백 페이지로 리다이렉트
        response.sendRedirect("http://localhost:3000/oauth/callback?token=" + accessToken);
    }

}