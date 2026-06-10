package com.busanit401.spring_back.domain.repository;

import com.busanit401.spring_back.domain.PaymentRefund;
import com.busanit401.spring_back.domain.repository.custom.PaymentRefundRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

public interface PaymentRefundRepository extends JpaRepository<PaymentRefund, Long>,
        PaymentRefundRepositoryCustom {

    // 특정 결제의 전체 환불 내역 조회
    List<PaymentRefund> findAllByPaymentId(Long paymentId);

    // 특정 결제의 특정 월 환불 조회 (중복 환불 방지)
    Optional<PaymentRefund> findByPaymentIdAndRefundMonth(Long paymentId, YearMonth refundMonth);

    // 특정 월 이미 환불됐는지 체크
    boolean existsByPaymentIdAndRefundMonth(Long paymentId, YearMonth refundMonth);
}