package com.busanit401.spring_back.domain.repository.custom;

import com.busanit401.spring_back.domain.QSubscriptionsUser;
import com.busanit401.spring_back.domain.SubscriptionsUser;
import com.busanit401.spring_back.dto.subscriptionsUser.SubscriptionSearchCondition;
import com.busanit401.spring_back.enums.SubscriptionStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
public class SubscriptionsUserRepositoryImpl implements SubscriptionsUserRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QSubscriptionsUser subscriptionsUser = QSubscriptionsUser.subscriptionsUser;

    @Override
    public List<SubscriptionsUser> searchByCondition(SubscriptionSearchCondition condition) {
        return queryFactory
                .selectFrom(subscriptionsUser)
                .where(
                        deletedAtIsNull(),
                        usernameContains(condition.getUsername()),
                        statusEq(condition.getStatus()),
                        startDateAfter(condition.getStartDate()),
                        endDateBefore(condition.getEndDate())
                )
                .orderBy(subscriptionsUser.createdDate.desc())
                .fetch();
    }


    // null이면 조건 무시 — 조건이 없으면 전체 조회
    private BooleanExpression deletedAtIsNull() {
        return subscriptionsUser.deletedAt.isNull();
    }

    private BooleanExpression usernameContains(String username) {
        return username != null ? subscriptionsUser.user.username.containsIgnoreCase(username) : null;
    }

    private BooleanExpression statusEq(SubscriptionStatus status) {
        return status != null ? subscriptionsUser.status.eq(status) : null;
    }

    private BooleanExpression startDateAfter(LocalDate startDate) {
        return startDate != null ? subscriptionsUser.startDate.goe(startDate) : null;
    }

    private BooleanExpression endDateBefore(LocalDate endDate) {
        return endDate != null ? subscriptionsUser.endDate.loe(endDate) : null;
    }
}
