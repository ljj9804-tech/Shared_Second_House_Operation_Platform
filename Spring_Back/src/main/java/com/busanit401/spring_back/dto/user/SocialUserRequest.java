package com.busanit401.spring_back.dto.user;

import com.busanit401.spring_back.enums.Role;
import com.busanit401.spring_back.security.oauth.OAuth2UserInfo;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocialUserRequest {


    private String username;
    private String password;
    private String nickname;
    private String email;
    private Role role;

    public static SocialUserRequest from(OAuth2UserInfo userInfo) {
        return SocialUserRequest.builder()
                .username(userInfo.getProvider() + "_" + userInfo.getProviderId())
                .password(null)
                .nickname(userInfo.getName() + "_" + userInfo.getProviderId())
                .email(userInfo.getEmail())
                .role(Role.SOCIAL)
                .build();
    }
}
