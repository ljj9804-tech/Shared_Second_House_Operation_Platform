package com.busanit401.spring_back.domain.repository;

import com.busanit401.spring_back.domain.Payment;
import com.busanit401.spring_back.enums.PaymentStatus;
import com.busanit401.spring_back.domain.repository.custom.PaymentRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long>,
        PaymentRepositoryCustom {

    // 특정 구독의 결제 조회
    Optional<Payment> findBySubscriptionsUserId(Long subscriptionsUserId);

    // 토스페이먼츠 paymentKey로 조회 (환불 시 필수)
    Optional<Payment> findByPaymentKey(String paymentKey);

    // 토스페이먼츠 orderId로 조회
    Optional<Payment> findByOrderId(String orderId);

    // 상태별 결제 목록 (관리자 페이지)
    List<Payment> findAllByStatus(PaymentStatus status);
}