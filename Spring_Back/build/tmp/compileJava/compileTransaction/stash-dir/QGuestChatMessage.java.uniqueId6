package com.busanit401.spring_back.domain.entity.guestChat;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QGuestChatMessage is a Querydsl query type for GuestChatMessage
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QGuestChatMessage extends EntityPathBase<GuestChatMessage> {

    private static final long serialVersionUID = -1322179185L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QGuestChatMessage guestChatMessage = new QGuestChatMessage("guestChatMessage");

    public final QGuestChatRoom guestChatRoom;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath messageContent = createString("messageContent");

    public final NumberPath<Long> senderId = createNumber("senderId", Long.class);

    public final StringPath senderName = createString("senderName");

    public final DateTimePath<java.time.LocalDateTime> sentAt = createDateTime("sentAt", java.time.LocalDateTime.class);

    public QGuestChatMessage(String variable) {
        this(GuestChatMessage.class, forVariable(variable), INITS);
    }

    public QGuestChatMessage(Path<? extends GuestChatMessage> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QGuestChatMessage(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QGuestChatMessage(PathMetadata metadata, PathInits inits) {
        this(GuestChatMessage.class, metadata, inits);
    }

    public QGuestChatMessage(Class<? extends GuestChatMessage> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.guestChatRoom = inits.isInitialized("guestChatRoom") ? new QGuestChatRoom(forProperty("guestChatRoom")) : null;
    }

}

