package com.busanit401.spring_back.dto.user;

import com.busanit401.spring_back.domain.User;
import com.busanit401.spring_back.enums.Role;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResp {

    private Long userId;

    private String username;

    private String password;

    private String email;

    private String nickname;

    private String phoneNumber;

    private Role role;

    public static UserResp from(User user) {
        return UserResp.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .build();
    }
}