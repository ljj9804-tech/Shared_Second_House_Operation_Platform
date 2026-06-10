package com.busanit401.spring_back.domain.repository.custom;


import com.busanit401.spring_back.domain.QWaitingSubscriptionUser;
import com.busanit401.spring_back.domain.WaitingSubscriptionUser;
import com.busanit401.spring_back.dto.waitingSubscriptionUser.WaitingSubscriptionSearchCondition;
import com.busanit401.spring_back.enums.MemberRole;
import com.busanit401.spring_back.enums.MemberStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import java.util.List;

@RequiredArgsConstructor
public class WaitingSubscriptionUserRepositoryImpl implements WaitingSubscriptionUserRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QWaitingSubscriptionUser waitingSubscriptionUser
            = QWaitingSubscriptionUser.waitingSubscriptionUser;

    @Override
    public List<WaitingSubscriptionUser> searchByCondition(WaitingSubscriptionSearchCondition condition) {
        return queryFactory
                .selectFrom(waitingSubscriptionUser)
                .where(
                        usernameContains(condition.getUsername()),
                        statusEq(condition.getStatus()),
                        memberRoleEq(condition.getMemberRole())
                )
                .orderBy(waitingSubscriptionUser.requestedAt.desc())
                .fetch();
    }

    private BooleanExpression usernameContains(String username) {
        return username != null
                ? waitingSubscriptionUser.user.username.containsIgnoreCase(username) : null;
    }

    private BooleanExpression statusEq(MemberStatus status) {
        return status != null ? waitingSubscriptionUser.status.eq(status) : null;
    }

    private BooleanExpression memberRoleEq(MemberRole memberRole) {
        return memberRole != null ? waitingSubscriptionUser.memberRole.eq(memberRole) : null;
    }
}