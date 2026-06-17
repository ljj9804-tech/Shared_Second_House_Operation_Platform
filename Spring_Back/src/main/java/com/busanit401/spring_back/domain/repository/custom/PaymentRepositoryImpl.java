package com.busanit401.spring_back.domain.repository.custom;

import com.busanit401.spring_back.domain.Payment;
import com.busanit401.spring_back.domain.QPayment;
import com.busanit401.spring_back.dto.PaymentSearchCondition;
import com.busanit401.spring_back.enums.PaymentStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QPayment payment = QPayment.payment;

    @Override
    public List<Payment> searchByCondition(PaymentSearchCondition condition) {
        return queryFactory
                .selectFrom(payment)
                .where(
                        usernameContains(condition.getUsername()),
                        statusEq(condition.getStatus()),
                        paidAtAfter(condition.getStartDate()),
                        paidAtBefore(condition.getEndDate())
                )
                .orderBy(payment.paidAt.desc())
                .fetch();
    }

    private BooleanExpression usernameContains(String username) {
        return username != null
                ? payment.subscriptionsUser.user.username.containsIgnoreCase(username) : null;
    }

    private BooleanExpression statusEq(PaymentStatus status) {
        return status != null ? payment.status.eq(status) : null;
    }

    private BooleanExpression paidAtAfter(LocalDateTime startDate) {
        return startDate != null ? payment.paidAt.goe(startDate) : null;
    }

    private BooleanExpression paidAtBefore(LocalDateTime endDate) {
        return endDate != null ? payment.paidAt.loe(endDate) : null;
    }
}
