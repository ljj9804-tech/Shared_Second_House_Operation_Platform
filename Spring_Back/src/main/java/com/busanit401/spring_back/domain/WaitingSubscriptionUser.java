package com.busanit401.spring_back.domain;

import com.busanit401.spring_back.enums.MemberRole;
import com.busanit401.spring_back.enums.MemberStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Table(name = "waiting_subscription_user")
public class WaitingSubscriptionUser {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "waiting_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id", nullable = false)
    private SubscriptionsUser subscriptionsUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "member_role", nullable = false)
    private MemberRole memberRole;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MemberStatus status;

    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;


    // 동의
    public void approve() {
        this.status = MemberStatus.APPROVED;
        this.respondedAt = LocalDateTime.now();
    }

    // 거절
    public void reject() {
        this.status = MemberStatus.REJECTED;
        this.respondedAt = LocalDateTime.now();
    }

    // 대표 생성
    public static WaitingSubscriptionUser createLeader(SubscriptionsUser subscriptionsUser, User leader) {
        return WaitingSubscriptionUser.builder()
                .subscriptionsUser(subscriptionsUser)
                .user(leader)
                .memberRole(MemberRole.LEADER)
                .status(MemberStatus.APPROVED)  // 대표는 자동 승인
                .requestedAt(LocalDateTime.now())
                .respondedAt(LocalDateTime.now())
                .build();
    }

    // 멤버 생성
    public static WaitingSubscriptionUser createMember(SubscriptionsUser subscriptionsUser, User member) {
        return WaitingSubscriptionUser.builder()
                .subscriptionsUser(subscriptionsUser)
                .user(member)
                .memberRole(MemberRole.MEMBER)
                .status(MemberStatus.PENDING)  // 멤버는 동의 대기
                .requestedAt(LocalDateTime.now())
                .build();
    }
}