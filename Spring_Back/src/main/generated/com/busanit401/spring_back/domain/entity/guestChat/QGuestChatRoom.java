package com.busanit401.spring_back.domain.entity.guestChat;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QGuestChatRoom is a Querydsl query type for GuestChatRoom
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QGuestChatRoom extends EntityPathBase<GuestChatRoom> {

    private static final long serialVersionUID = -1628141485L;

    public static final QGuestChatRoom guestChatRoom = new QGuestChatRoom("guestChatRoom");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> houseId = createNumber("houseId", Long.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath roomName = createString("roomName");

    public QGuestChatRoom(String variable) {
        super(GuestChatRoom.class, forVariable(variable));
    }

    public QGuestChatRoom(Path<? extends GuestChatRoom> path) {
        super(path.getType(), path.getMetadata());
    }

    public QGuestChatRoom(PathMetadata metadata) {
        super(GuestChatRoom.class, metadata);
    }

}

