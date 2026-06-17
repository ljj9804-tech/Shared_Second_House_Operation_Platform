package com.busanit401.spring_back.domain.repository.custom;

import com.busanit401.spring_back.domain.PaymentRefund;
import com.busanit401.spring_back.domain.QPaymentRefund;
import com.busanit401.spring_back.dto.PaymentRefund.PaymentRefundSearchCondition;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import java.time.YearMonth;
import java.util.List;

@RequiredArgsConstructor
public class PaymentRefundRepositoryImpl implements PaymentRefundRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QPaymentRefund paymentRefund = QPaymentRefund.paymentRefund;

    @Override
    public List<PaymentRefund> searchByCondition(PaymentRefundSearchCondition condition) {
        return queryFactory
                .selectFrom(paymentRefund)
                .where(
                        usernameContains(condition.getUsername()),
                        refundMonthEq(condition.getRefundMonth())
                )
                .orderBy(paymentRefund.refundedAt.desc())
                .fetch();
    }

    private BooleanExpression usernameContains(String username) {
        return username != null
                ? paymentRefund.payment.subscriptionsUser.user.username
                .containsIgnoreCase(username) : null;
    }

    private BooleanExpression refundMonthEq(YearMonth refundMonth) {
        return refundMonth != null ? paymentRefund.refundMonth.eq(refundMonth) : null;
    }
}
