package com.busanit401.spring_back.security.oauth;

import com.busanit401.spring_back.domain.User;
import com.busanit401.spring_back.dto.user.SocialUserRequest;
import com.busanit401.spring_back.domain.repository.UserRepository;
import com.busanit401.spring_back.security.auth.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
@Service
@Transactional
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        OAuth2UserInfo userInfo = getOAuth2UserInfo(registrationId, attributes);

        User user = createUserFromUserInfo(userInfo);

        if (!userRepository.existsByUsername(user.getUsername())) {
            userRepository.save(user);
        } else {
            updateUser(user);
        }

        return new CustomUserDetails(user, attributes);
    }

    private OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        if (registrationId.equals("google")) {
            return new GoogleUserInfo(attributes);
        } else if (registrationId.equals("naver")) {
            return new NaverUserInfo((Map<String, Object>) attributes.get("response"));
        } else {
            throw new OAuth2AuthenticationException("Unsupported provider: " + registrationId);
        }
    }

    private User createUserFromUserInfo(OAuth2UserInfo userInfo) {

        return User.from(SocialUserRequest.from(userInfo));
    }

    private void updateUser(User user) {
        // 필요시 사용자 정보를 업데이트하는 로직 추가
        // 예: user.setLastLogin(new Date());
        userRepository.save(user);
    }
}
