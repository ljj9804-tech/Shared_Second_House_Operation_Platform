package com.busanit401.spring_back.domain;

import com.busanit401.spring_back.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Table(name = "user")
public class User extends  BaseTimeEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @Builder.Default
    private List<SubscriptionsUser>  subscriptions = new ArrayList<>();



    @Column(name = "user_name", nullable = false, unique = true)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "nick_name", nullable = false, unique = true)
    private String nickname;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;


    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;


    //비지니스 로직
    public void update(String password, String nickname) {
        this.password = password;
        this.nickname = nickname;
    }


    //비지니스 로직
    public void updatePassword(String password) {
        this.password = password;
    }


//    public static User from(SocialUserRequest userRequest) {
//        return User.builder()
//                .username(userRequest.getUsername())
//                .password(userRequest.getPassword())
//                .nickname(userRequest.getNickname())
//                .email(userRequest.getEmail())
//                .role(userRequest.getRole())
//                .build();
//    }
//
//    public static User from(UserResp response) {
//        return User.builder()
//                .id(response.getUserId())
//                .username(response.getUsername())
//                .password(response.getPassword())
//                .email(response.getEmail())
//                .nickname(response.getNickname())
//                .phoneNumber(response.getPhoneNumber())
//                .role(response.getRole())
//                .build();
//    }
//
//    public static User from(UserReq request) {
//        return User.builder()
//                .username(request.getUsername())
//                .password(request.getPassword())
//                .nickname(request.getNickname())
//                .email(request.getEmail())
//                .phoneNumber(request.getPhoneNumber())
//                .role(request.getRole())
//                .build();
//    }




}
