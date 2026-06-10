package com.busanit401.spring_back.controller;

import com.busanit401.spring_back.dto.PaymentSearchCondition;
import com.busanit401.spring_back.dto.payment.PaymentReq;
import com.busanit401.spring_back.dto.payment.PaymentRefundReq;
import com.busanit401.spring_back.dto.payment.PaymentRefundResp;
import com.busanit401.spring_back.dto.payment.PaymentResp;
import com.busanit401.spring_back.domain.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // 결제 완료 (토스페이먼츠 승인 후 호출)
    @PostMapping("/confirm")
    public ResponseEntity<PaymentResp> confirm(@RequestBody @Valid PaymentReq req) {
        return ResponseEntity.ok(paymentService.completePayment(
                req.getPaymentKey(), req.getOrderId()));
    }

    // 결제 실패
    @PostMapping("/fail")
    public ResponseEntity<PaymentResp> fail(@RequestParam String paymentKey) {
        return ResponseEntity.ok(paymentService.failPayment(paymentKey));
    }

    // 부분 환불
    @PostMapping("/{paymentId}/refund")
    public ResponseEntity<PaymentRefundResp> refund(
            @PathVariable Long paymentId,
            @RequestBody @Valid PaymentRefundReq req) {
        return ResponseEntity.ok(paymentService.refundMonth(paymentId, req));
    }

    // 결제 조회
    @GetMapping("/{subscriptionsUserId}")
    public ResponseEntity<PaymentResp> getPayment(
            @PathVariable Long subscriptionsUserId) {
        return ResponseEntity.ok(paymentService.getPayment(subscriptionsUserId));
    }

    // 환불 내역 조회
    @GetMapping("/{paymentId}/refunds")
    public ResponseEntity<List<PaymentRefundResp>> getRefunds(
            @PathVariable Long paymentId) {
        return ResponseEntity.ok(paymentService.getRefunds(paymentId));
    }

    // 관리자 - 복합 조건 검색
    @GetMapping("/admin/search")
    public ResponseEntity<List<PaymentResp>> searchByCondition(
            PaymentSearchCondition condition) {
        return ResponseEntity.ok(paymentService.searchByCondition(condition));
    }
}