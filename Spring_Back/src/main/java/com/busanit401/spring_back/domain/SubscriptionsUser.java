package com.busanit401.spring_back.domain;

import com.busanit401.spring_back.enums.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Table(name = "subscription")

public class SubscriptionsUser extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subscription_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    @Column(name = "duration_months", nullable = false)
    private int durationMonths;  // 1 ~ 12

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SubscriptionStatus status;

    @OneToOne(mappedBy = "subscription", fetch = FetchType.LAZY)
    private Payment payment;

    // 구독 취소
    public void cancel() {
        this.status = SubscriptionStatus.CANCELLED;
    }

    // 구독 만료
    public void expire() {
        this.status = SubscriptionStatus.EXPIRED;
    }

}