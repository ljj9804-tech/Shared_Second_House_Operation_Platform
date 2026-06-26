package com.busanit401.spring_back.controller;

import com.busanit401.spring_back.domain.service.OrderService;
import com.busanit401.spring_back.dto.OrderRequestDto;
import com.busanit401.spring_back.dto.OrderResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService orderService;

    // POST: 주문 생성
    @PostMapping
    public ResponseEntity<?> placeOrder(@RequestBody OrderRequestDto orderRequest) {
        try {
            Long orderId = orderService.createOrder(orderRequest);
            return ResponseEntity.ok(Map.of("success", true, "orderId", orderId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    } // <-- 이 닫는 중괄호가 반드시 있어야 합니다.

    // GET: 주문 목록 조회
    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> getOrders() {
        List<OrderResponseDto> orderDtos = orderService.getAllOrders().stream()
                .map(order -> OrderResponseDto.builder()
                        .order_id(order.getOrderId())
                        .user_id(order.getUserId())
                        .delivery_address(order.getDeliveryAddress())
                        .total_amount(order.getTotalAmount())
                        .status(order.getStatus())
                        .build())
                .toList();

        return ResponseEntity.ok(orderDtos);
    }
}