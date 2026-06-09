package com.busanit401.spring_back.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPaymentRefund is a Querydsl query type for PaymentRefund
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPaymentRefund extends EntityPathBase<PaymentRefund> {

    private static final long serialVersionUID = -1542132339L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPaymentRefund paymentRefund = new QPaymentRefund("paymentRefund");

    public final StringPath cancelKey = createString("cancelKey");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QPayment payment;

    public final NumberPath<Long> refundAmount = createNumber("refundAmount", Long.class);

    public final DateTimePath<java.time.LocalDateTime> refundedAt = createDateTime("refundedAt", java.time.LocalDateTime.class);

    public final ComparablePath<java.time.YearMonth> refundMonth = createComparable("refundMonth", java.time.YearMonth.class);

    public QPaymentRefund(String variable) {
        this(PaymentRefund.class, forVariable(variable), INITS);
    }

    public QPaymentRefund(Path<? extends PaymentRefund> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPaymentRefund(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPaymentRefund(PathMetadata metadata, PathInits inits) {
        this(PaymentRefund.class, metadata, inits);
    }

    public QPaymentRefund(Class<? extends PaymentRefund> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.payment = inits.isInitialized("payment") ? new QPayment(forProperty("payment"), inits.get("payment")) : null;
    }

}

