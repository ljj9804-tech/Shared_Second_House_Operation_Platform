package com.busanit401.spring_back.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
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

    // 상태 변경용 Setter 메서드 대신 비즈니스 메서드 사용
    public void changeStatus(String status) {
        this.status = status;
    }
}
