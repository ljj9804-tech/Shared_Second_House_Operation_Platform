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
    public List<SubscriptionsUserResp> getMySubscriptions(Long userId) {
        return subscriptionsUserRepository.findAllByUserId(userId)
                .stream()
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