package com.busanit401.spring_back.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // "이제부터 실시간 채팅 서버 기능을 켜겠다"는 선언
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 2. 플러터 앱이 처음 서버와 연결을 시도할 주소
        registry.addEndpoint("/ws-guest-chat")
                .setAllowedOriginPatterns("*");
        // CORS 차단 해제: 전역으로 설정(이후 사용자 경로로 재설정)
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 서버 -> 클라이언트
        registry.enableSimpleBroker("/topic");

        // 클라이언트 -> 서버
        registry.setApplicationDestinationPrefixes("/app");
    }
}

/* ====================================================================
[환경설정] 실시간 채팅을 위한 웹소켓 및 STOMP 기본 인프라 설정

웹소켓 연결 문(registerStompEndpoints)
연결 엔드포인트: //localhost:8080/ws-guest-chat
CORS 차단 해제: .setAllowedOriginPatterns("*")

configureMessageBroker
서버 -> 클라이언트 ("/topic")
    -> 예시 경로: /topic/guest/room/{방번호}
클라이언트 -> 서버("/app")
    -> 예시 경로: /app/guest/chat/send

 */