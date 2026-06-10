package com.busanit401.spring_back.domain.repository;

import com.busanit401.spring_back.domain.WaitingSubscriptionUser;
import com.busanit401.spring_back.enums.MemberStatus;
import com.busanit401.spring_back.domain.repository.custom.WaitingSubscriptionUserRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface WaitingSubscriptionUserRepository extends JpaRepository<WaitingSubscriptionUser, Long>,
        WaitingSubscriptionUserRepositoryCustom {

    // 특정 구독의 전체 대기 멤버 조회
    List<WaitingSubscriptionUser> findAllBySubscriptionsUserId(Long subscriptionsUserId);

    // 특정 구독에서 특정 유저 조회 (동의/거절 시)
    Optional<WaitingSubscriptionUser> findBySubscriptionsUserIdAndUserId(Long subscriptionsUserId, Long userId);

    // 특정 유저의 대기 중인 초대 목록 (내가 초대받은 구독 목록)
    List<WaitingSubscriptionUser> findAllByUserIdAndStatus(Long userId, MemberStatus status);

    // 이미 초대된 유저인지 중복 체크
    boolean existsBySubscriptionsUserIdAndUserId(Long subscriptionsUserId, Long userId);
}