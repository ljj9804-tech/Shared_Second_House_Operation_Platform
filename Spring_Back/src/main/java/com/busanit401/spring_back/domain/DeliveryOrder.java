package com.busanit401.spring_back.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
// 🚨 진주 팀원의 DB 테이블 규칙 반영: 테이블명 앞에 sh_ 붙이기!
@Table(name = "sh_orders")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DeliveryOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private int totalAmount;

    @Column(nullable = false, length = 500)
    private String deliveryAddress;

    @Builder.Default
    @Column(nullable = false, length = 50)
    private String status = "주문완료"; // 주문완료 -> 배송중 -> 배송완료

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    /**
     * 💡 배송 상태 변경용 비즈니스 메서드
     * (플러터 및 내부 단위 테스트에서 호출하여 사용합니다)
     */
    public void changeStatus(String status) {
        this.status = status;
    }

    public void updateStatus(String status) {
        this.status = status;
    }
}
