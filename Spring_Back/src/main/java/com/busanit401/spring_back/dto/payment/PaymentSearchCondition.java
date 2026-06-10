package com.busanit401.spring_back.dto;

import com.busanit401.spring_back.enums.PaymentStatus;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class PaymentSearchCondition {
    private String username;
    private PaymentStatus status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}