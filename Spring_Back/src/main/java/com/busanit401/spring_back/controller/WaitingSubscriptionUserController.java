package com.busanit401.spring_back.controller;

import com.busanit401.spring_back.dto.waitingSubscriptionUser.WaitingSubscriptionReq;
import com.busanit401.spring_back.dto.waitingSubscriptionUser.WaitingSubscriptionResp;
import com.busanit401.spring_back.dto.waitingSubscriptionUser.WaitingSubscriptionSearchCondition;
import com.busanit401.spring_back.domain.service.WaitingSubscriptionUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/waiting")
@RequiredArgsConstructor
public class WaitingSubscriptionUserController {

    private final WaitingSubscriptionUserService waitingService;

    // 구독 신청 (대표)
    @PostMapping("/apply/{leaderId}")
    public ResponseEntity<List<WaitingSubscriptionResp>> apply(
            @PathVariable Long leaderId,
            @RequestBody @Valid WaitingSubscriptionReq req) {
        return ResponseEntity.status(201).body(waitingService.apply(leaderId, req));
    }

    // 멤버 동의
    @PostMapping("/{subscriptionsUserId}/approve/{userId}")
    public ResponseEntity<WaitingSubscriptionResp> approve(
            @PathVariable Long subscriptionsUserId,
            @PathVariable Long userId) {
        return ResponseEntity.ok(waitingService.approveMember(subscriptionsUserId, userId));
    }

    // 멤버 거절
    @PostMapping("/{subscriptionsUserId}/reject/{userId}")
    public ResponseEntity<WaitingSubscriptionResp> reject(
            @PathVariable Long subscriptionsUserId,
            @PathVariable Long userId) {
        return ResponseEntity.ok(waitingService.rejectMember(subscriptionsUserId, userId));
    }

    // 내 초대 목록 조회
    @GetMapping("/my/{userId}")
    public ResponseEntity<List<WaitingSubscriptionResp>> getMyInvitations(
            @PathVariable Long userId) {
        return ResponseEntity.ok(waitingService.getMyInvitations(userId));
    }

    // 관리자 - 복합 조건 검색
    @GetMapping("/admin/search")
    public ResponseEntity<List<WaitingSubscriptionResp>> searchByCondition(
            WaitingSubscriptionSearchCondition condition) {
        return ResponseEntity.ok(waitingService.searchByCondition(condition));
    }
}