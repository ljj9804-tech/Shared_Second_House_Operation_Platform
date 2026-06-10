package com.busanit401.spring_back.domain.repository;

import com.busanit401.spring_back.domain.SubscriptionsUser;
import com.busanit401.spring_back.enums.SubscriptionStatus;
import com.busanit401.spring_back.domain.repository.custom.SubscriptionsUserRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SubscriptionsUserRepository extends JpaRepository<SubscriptionsUser, Long>, SubscriptionsUserRepositoryCustom {

    // 특정 유저의 특정 구독 조회 (취소 시 사용)
    Optional<SubscriptionsUser> findByIdAndUserId(Long subscriptionId, Long userId);

    // 특정 유저의 전체 구독 목록
    List<SubscriptionsUser> findAllByUserId(Long userId);

    // 상태별 구독 목록 (관리자 페이지)
    List<SubscriptionsUser> findAllByStatus(SubscriptionStatus status);

    // 같은 숙소 중복 구독 체크
    boolean existsByUserIdAndAccommodationIdAndStatus(Long userId, Long accommodationId, SubscriptionStatus status);
}