package com.busanit401.spring_back.domain.repository.custom;

import com.busanit401.spring_back.domain.SubscriptionsUser;
import com.busanit401.spring_back.dto.subscriptionsUser.SubscriptionSearchCondition;

import java.util.List;

public interface SubscriptionsUserRepositoryCustom {

    // 관리자 페이지 복합 조건 검색
    List<SubscriptionsUser> searchByCondition(SubscriptionSearchCondition subscriptionSearchCondition);
}
