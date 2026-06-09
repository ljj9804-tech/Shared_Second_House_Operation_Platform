package com.busanit401.spring_back.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSubscriptionsUser is a Querydsl query type for SubscriptionsUser
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSubscriptionsUser extends EntityPathBase<SubscriptionsUser> {

    private static final long serialVersionUID = 2134703312L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSubscriptionsUser subscriptionsUser = new QSubscriptionsUser("subscriptionsUser");

    public final QBaseTimeEntity _super = new QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final NumberPath<Integer> durationMonths = createNumber("durationMonths", Integer.class);

    public final DatePath<java.time.LocalDate> endDate = createDate("endDate", java.time.LocalDate.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedDate = _super.modifiedDate;

    public final QPayment payment;

    public final DatePath<java.time.LocalDate> startDate = createDate("startDate", java.time.LocalDate.class);

    public final EnumPath<com.busanit401.spring_back.enums.SubscriptionStatus> status = createEnum("status", com.busanit401.spring_back.enums.SubscriptionStatus.class);

    public final QUser user;

    public QSubscriptionsUser(String variable) {
        this(SubscriptionsUser.class, forVariable(variable), INITS);
    }

    public QSubscriptionsUser(Path<? extends SubscriptionsUser> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSubscriptionsUser(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSubscriptionsUser(PathMetadata metadata, PathInits inits) {
        this(SubscriptionsUser.class, metadata, inits);
    }

    public QSubscriptionsUser(Class<? extends SubscriptionsUser> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.payment = inits.isInitialized("payment") ? new QPayment(forProperty("payment"), inits.get("payment")) : null;
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user")) : null;
    }

}

