package com.busanit401.spring_back.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 브라우저에서 /uploads/** 로 접근하면
        registry.addResourceHandler("/uploads/**")
                // 실제 서버 컴퓨터의 프로젝트 루트/uploads/ 폴더와 매핑합니다.
                // 정적 리소스 매핑 시 경로 끝에 '/'는 필수입니다.
                .addResourceLocations("file:uploads/");
    }
}