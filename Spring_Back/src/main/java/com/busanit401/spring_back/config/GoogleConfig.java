package com.busanit401.spring_back.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class GoogleConfig {

    /** Google API 공용 WebClient (Gemini 임베딩·생성, Places 등).
     * baseUrl을 두지 않고, 호출 URL은 각 클라이언트가 절대경로로 직접 지정한다. */
    @Bean
    public WebClient googleWebClient() {
        return WebClient.builder().build();
    }
}
