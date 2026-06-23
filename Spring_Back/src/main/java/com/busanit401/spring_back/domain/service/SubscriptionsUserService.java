package com.busanit401.spring_back.domain.service;

import com.busanit401.spring_back.domain.SubscriptionsUser;
import com.busanit401.spring_back.dto.subscriptionsUser.SubscriptionSearchCondition;
import com.busanit401.spring_back.dto.subscriptionsUser.SubscriptionsUserResp;
import com.busanit401.spring_back.exception.BusinessException;
import com.busanit401.spring_back.exception.EntityNotFoundException;
import com.busanit401.spring_back.exception.ErrorCode;
import com.busanit401.spring_back.exception.InvalidStateException;
import com.busanit401.spring_back.enums.SubscriptionStatus;
import com.busanit401.spring_back.domain.repository.SubscriptionsUserRepository;
import com.busanit401.spring_back.domain.repository.UserRepository;
import com.busanit401.spring_back.domain.repository.WaitingSubscriptionUserRepository;
import com.busanit401.spring_back.enums.MemberStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubscriptionsUserService {

    private final SubscriptionsUserRepository subscriptionsUserRepository;
    private final UserRepository userRepository;
    private final WaitingSubscriptionUserRepository waitingSubscriptionUserRepository;


    // 관리자 - 구독 승인
    @Transactional
    public SubscriptionsUserResp approve(Long subscriptionId) {
        SubscriptionsUser subscriptionsUser = findPendingSubscription(subscriptionId);
        subscriptionsUser.approve();
        log.info("[구독 승인] subscriptionId: {}", subscriptionId);
        return SubscriptionsUserResp.from(subscriptionsUser);
    }


    // 관리자 - 구독 반려
    @Transactional
    public SubscriptionsUserResp reject(Long subscriptionId) {
        SubscriptionsUser subscriptionsUser = findPendingSubscription(subscriptionId);
        subscriptionsUser.reject();
        log.info("[구독 반려] subscriptionId: {}", subscriptionId);
        return SubscriptionsUserResp.from(subscriptionsUser);
    }


    // 구독 취소 (대표만 가능)
    @Transactional
    public SubscriptionsUserResp cancel(Long subscriptionId, Long userId) {
        SubscriptionsUser subscriptionsUser = findActiveSubscription(subscriptionId);

        // 대표 유저인지 체크
        validateLeader(subscriptionsUser, userId);

        subscriptionsUser.cancel();
        log.info("[구독 취소] subscriptionId: {}, userId: {}", subscriptionId, userId);
        return SubscriptionsUserResp.from(subscriptionsUser);
    }


    // 구독 만료 처리 (스케줄러에서 호출)
    @Transactional
    public void expireSubscriptions() {
        List<SubscriptionsUser> expiredList = subscriptionsUserRepository
                .findAllByStatus(SubscriptionStatus.ACTIVE)
                .stream()
                .filter(s -> s.getEndDate().isBefore(LocalDate.now()))
                .collect(Collectors.toList());

        expiredList.forEach(SubscriptionsUser::expire);
        log.info("[구독 만료 처리] 처리 건수: {}", expiredList.size());
    }


    // 내 구독 목록 조회
    // [수정 이유] 기존 코드는 subscriptions_user.user_id = userId (대표자만) 조회 2026-06-20
    //            → 멤버로 참여한 구독은 반환되지 않아 멤버 유저가 구독 상태를 확인할 수 없었음
    //            → waiting_subscription_user에서 APPROVED된 구독도 함께 반환하도록 수정
    public List<SubscriptionsUserResp> getMySubscriptions(Long userId) {
        // 대표자로 신청한 구독
        List<SubscriptionsUser> leaderSubscriptions =
                subscriptionsUserRepository.findAllByUserId(userId);

        // 멤버로 참여하고 APPROVED된 구독
        List<SubscriptionsUser> memberSubscriptions =
                waitingSubscriptionUserRepository.findAllByUserIdAndStatus(userId, MemberStatus.APPROVED)
                        .stream()
                        .map(w -> w.getSubscriptionsUser())
                        .distinct()
                        .collect(Collectors.toList());

        // 합치고 중복 제거 후 반환
        return java.util.stream.Stream.concat(leaderSubscriptions.stream(), memberSubscriptions.stream())
                .distinct()
                .map(SubscriptionsUserResp::from)
                .collect(Collectors.toList());
    }


    // 단일 구독 상세 조회
    public SubscriptionsUserResp getSubscription(Long subscriptionId) {
        return SubscriptionsUserResp.from(findSubscription(subscriptionId));
    }


    // 관리자 - 복합 조건 검색
    public List<SubscriptionsUserResp> searchByCondition(SubscriptionSearchCondition condition) {
        return subscriptionsUserRepository.searchByCondition(condition)
                .stream()
                .map(SubscriptionsUserResp::from)
                .collect(Collectors.toList());
    }


    // -----------------------------------------------
    // Private 메서드
    // -----------------------------------------------

    // 구독 조회
    private SubscriptionsUser findSubscription(Long subscriptionId) {
        return subscriptionsUserRepository.findById(subscriptionId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.SUBSCRIPTION_NOT_FOUND));
    }

    // PENDING 상태 구독 조회 (승인/반려 시 사용)
    private SubscriptionsUser findPendingSubscription(Long subscriptionId) {
        SubscriptionsUser subscriptionsUser = findSubscription(subscriptionId);
        if (subscriptionsUser.getStatus() != SubscriptionStatus.PENDING) {
            throw new InvalidStateException(ErrorCode.ALREADY_PROCESSED);
        }
        return subscriptionsUser;
    }

    // ACTIVE 상태 구독 조회 (취소 시 사용)
    private SubscriptionsUser findActiveSubscription(Long subscriptionId) {
        SubscriptionsUser subscriptionsUser = findSubscription(subscriptionId);
        if (subscriptionsUser.getStatus() != SubscriptionStatus.ACTIVE) {
            throw new InvalidStateException(ErrorCode.ALREADY_PROCESSED);
        }
        return subscriptionsUser;
    }

    // 대표 유저인지 체크
    private void validateLeader(SubscriptionsUser subscriptionsUser, Long userId) {
        if (!subscriptionsUser.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT);
        }
    }
}