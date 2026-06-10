package com.busanit401.spring_back.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginUserReq {

    @NotBlank( message = "아이디 입력은 필수입니다.")
    private String username;

    @NotBlank( message = "비밀번호 입력은 필수입니다.")
    private String password;

}