package com.busanit401.spring_back.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@Getter
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    @Value("${jwt.refreshExpiration}")
    private Long refreshExpiration;

    private SecretKey key;

    @PostConstruct
    public void init() {
        // [수정] UTF_8을 명시해 주는 것이 실무 표준이며 인코딩 에러를 방지합니다.
        key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username) // [변경] setSubject() -> subject()
                .issuedAt(new Date()) // [변경] setIssuedAt() -> issuedAt()
                .expiration(new Date(System.currentTimeMillis() + expiration * 1000)) // [변경] setExpiration() -> expiration()
                .signWith(key) // [변경] 이제 알고리즘(HS512 등)을 명시하지 않아도 key의 길이를 보고 자동으로 최적의 알고리즘을 선택해 줍니다!
                .compact();
    }

    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(key)
                .compact();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public Claims extractAllClaims(String token) {
        // [변경] 문제의 원인! parserBuilder().setSigningKey().build().parseClaimsJws().getBody() 가 아래처럼 축약되었습니다.
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload(); // 기존의 getBody() 대신 getPayload()를 사용합니다.
    }

    public boolean isTokenValid(String token, String username) {
        return extractUsername(token).equals(username) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }
}