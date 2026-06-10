package com.busanit401.spring_back.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Builder
@Getter
@Setter
public class JwtResp {

    private String accessToken;
    private String refreshToken;
    private String username;
    private Long expiresIn;
    private Long refreshExpiresIn;

    public static JwtResp from(String accessToken, String refreshToken, String username, Long expiresIn, Long refreshExpiresIn) {
        return JwtResp.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .username(username)
                .expiresIn(expiresIn)
                .refreshExpiresIn(refreshExpiresIn)
                .build();

    }
}
