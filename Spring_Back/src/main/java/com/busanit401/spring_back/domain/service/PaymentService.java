package com.busanit401.spring_back.domain.service;

import com.busanit401.spring_back.domain.Payment;
import com.busanit401.spring_back.domain.PaymentRefund;
import com.busanit401.spring_back.domain.SubscriptionsUser;
import com.busanit401.spring_back.dto.PaymentSearchCondition;
import com.busanit401.spring_back.dto.payment.PaymentReq;
import com.busanit401.spring_back.dto.payment.PaymentRefundReq;
import com.busanit401.spring_back.dto.payment.PaymentRefundResp;
import com.busanit401.spring_back.dto.payment.PaymentResp;
import com.busanit401.spring_back.enums.NotificationType;
import com.busanit401.spring_back.enums.PaymentStatus;
import com.busanit401.spring_back.exception.BusinessException;
import com.busanit401.spring_back.exception.DuplicateException;
import com.busanit401.spring_back.exception.EntityNotFoundException;
import com.busanit401.spring_back.exception.ErrorCode;
import com.busanit401.spring_back.exception.InvalidStateException;
import com.busanit401.spring_back.domain.repository.PaymentRefundRepository;
import com.busanit401.spring_back.domain.repository.PaymentRepository;
import com.busanit401.spring_back.domain.repository.SubscriptionsUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentRefundRepository paymentRefundRepository;
    private final SubscriptionsUserRepository subscriptionsUserRepository;
    private final NotificationService notificationService;


    // 결제 생성 (구독 승인 후 결제창 띄우기 전)
    @Transactional
    public PaymentResp createPayment(PaymentReq req) {
        SubscriptionsUser subscriptionsUser = findSubscriptionsUser(req.getSubscriptionsUserId());

        // 이미 결제된 구독인지 체크
        if (paymentRepository.findBySubscriptionsUserId(
                req.getSubscriptionsUserId()).isPresent()) {
            throw new DuplicateException(ErrorCode.DUPLICATE_SUBSCRIPTION);
        }

        Payment payment = Payment.builder()
                .subscriptionsUser(subscriptionsUser)
                .paymentKey(req.getPaymentKey())
                .orderId(req.getOrderId())
                .amount(req.getAmount())
                .status(PaymentStatus.PENDING)
                .build();

        return PaymentResp.from(paymentRepository.save(payment));
    }


    // 결제 완료 (토스페이먼츠 승인 후 호출)
    @Transactional
    public PaymentResp completePayment(String paymentKey, String orderId) {
        Payment payment = findPaymentByPaymentKey(paymentKey);

        // orderId 일치 여부 체크
        if (!payment.getOrderId().equals(orderId)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT);
        }

        payment.complete(paymentKey, orderId);

        // 유저한테 결제 완료 알림
        notificationService.notifyUser(
                payment.getSubscriptionsUser().getUser().getId(),
                NotificationType.SUBSCRIPTION_APPROVED,
                "구독 결제가 완료되었습니다.",
                payment.getSubscriptionsUser().getId()
        );

        log.info("[결제 완료] paymentKey: {}", paymentKey);
        return PaymentResp.from(payment);
    }


    // 결제 실패 (토스페이먼츠 실패 후 호출)
    @Transactional
    public PaymentResp failPayment(String paymentKey) {
        Payment payment = findPaymentByPaymentKey(paymentKey);
        payment.fail();
        log.warn("[결제 실패] paymentKey: {}", paymentKey);
        return PaymentResp.from(payment);
    }


    // 부분 환불 (특정 월 환불)
    @Transactional
    public PaymentRefundResp refundMonth(Long paymentId, PaymentRefundReq req) {
        Payment payment = findPayment(paymentId);

        // 완료된 결제인지 체크
        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new InvalidStateException(ErrorCode.ALREADY_PROCESSED);
        }

        // 이미 환불된 월인지 체크
        if (paymentRefundRepository.existsByPaymentIdAndRefundMonth(
                paymentId, req.getRefundMonth())) {
            throw new DuplicateException(ErrorCode.ALREADY_REFUNDED);
        }

        // 환불 금액 초과 체크
        if (payment.getRefundedAmount() + req.getRefundAmount() > payment.getAmount()) {
            throw new BusinessException(ErrorCode.REFUND_AMOUNT_EXCEEDED);
        }

        // 환불 내역 생성
        PaymentRefund refund = PaymentRefund.builder()
                .payment(payment)
                .refundMonth(req.getRefundMonth())
                .refundAmount(req.getRefundAmount())
                .refundedAt(LocalDateTime.now())
                .build();

        paymentRefundRepository.save(refund);

        // Payment 환불 금액 누적
        payment.refundMonth(req.getRefundAmount());

        // 유저한테 환불 완료 알림
        notificationService.notifyUser(
                payment.getSubscriptionsUser().getUser().getId(),
                NotificationType.SUBSCRIPTION_CANCELLED,
                req.getRefundMonth() + " 환불이 완료되었습니다.",
                payment.getSubscriptionsUser().getId()
        );

        log.info("[부분 환불] paymentId: {}, refundMonth: {}, refundAmount: {}",
                paymentId, req.getRefundMonth(), req.getRefundAmount());
        return PaymentRefundResp.from(refund);
    }


    // 특정 구독의 결제 조회
    public PaymentResp getPayment(Long subscriptionsUserId) {
        return PaymentResp.from(
                paymentRepository.findBySubscriptionsUserId(subscriptionsUserId)
                        .orElseThrow(() -> new EntityNotFoundException(ErrorCode.PAYMENT_NOT_FOUND)));
    }


    // 특정 결제의 환불 내역 조회
    public List<PaymentRefundResp> getRefunds(Long paymentId) {
        return paymentRefundRepository.findAllByPaymentId(paymentId)
                .stream()
                .map(PaymentRefundResp::from)
                .collect(Collectors.toList());
    }


    // 관리자 - 복합 조건 검색
    public List<PaymentResp> searchByCondition(PaymentSearchCondition condition) {
        return paymentRepository.searchByCondition(condition)
                .stream()
                .map(PaymentResp::from)
                .collect(Collectors.toList());
    }


    // -----------------------------------------------
    // Private 메서드
    // -----------------------------------------------

    private Payment findPayment(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.PAYMENT_NOT_FOUND));
    }

    private Payment findPaymentByPaymentKey(String paymentKey) {
        return paymentRepository.findByPaymentKey(paymentKey)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.PAYMENT_NOT_FOUND));
    }

    private SubscriptionsUser findSubscriptionsUser(Long subscriptionsUserId) {
        return subscriptionsUserRepository.findById(subscriptionsUserId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.SUBSCRIPTION_NOT_FOUND));
    }
}