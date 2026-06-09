package com.busanit401.spring_back.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryOrderDTO {
    private Long orderId;
    private Long userId;
    private int totalAmount;
    private String deliveryAddress;
    private String status;
    private LocalDateTime createdAt;
}
