package com.busanit401.spring_back.dto.user;

import com.busanit401.spring_back.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class UserInfo {

    private Long userId;
    private String username;
    private String password;
    private String nickname;
    private String email;
    private String phoneNumber;
    private Role role;
}
