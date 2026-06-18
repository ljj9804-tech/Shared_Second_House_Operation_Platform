package com.busanit401.spring_back.domain.repository.custom;


import com.busanit401.spring_back.domain.WaitingSubscriptionUser;
import com.busanit401.spring_back.dto.waitingSubscriptionUser.WaitingSubscriptionSearchCondition;

import java.util.List;

public interface WaitingSubscriptionUserRepositoryCustom {

    // 관리자 페이지 복합 조건 검색
    List<WaitingSubscriptionUser> searchByCondition(WaitingSubscriptionSearchCondition condition);
}
