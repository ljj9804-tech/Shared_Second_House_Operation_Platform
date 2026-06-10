package com.busanit401.spring_back.domain.service;

import com.busanit401.spring_back.domain.repository.DeliveryOrderRepository;
import com.busanit401.spring_back.domain.DeliveryOrder;
import com.busanit401.spring_back.dto.DeliveryOrderDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryService {

    private final DeliveryOrderRepository repository;

    // Flutter 로부터 들어온 주문을 DB에 저장
    public Long createOrder(DeliveryOrderDTO dto) {
        DeliveryOrder order = DeliveryOrder.builder()
                .userId(dto.getUserId())
                .totalAmount(dto.getTotalAmount())
                .deliveryAddress(dto.getDeliveryAddress())
                .build();
        return repository.save(order).getOrderId();
    }

    // Next.js 관리자 웹을 위해 전체 주문 리스트 뽑기
    @Transactional(readOnly = true)
    public List<DeliveryOrderDTO> getAllOrders() {
        return repository.findAll().stream().map(order ->
                DeliveryOrderDTO.builder()
                        .orderId(order.getOrderId())
                        .userId(order.getUserId())
                        .totalAmount(order.getTotalAmount())
                        .deliveryAddress(order.getDeliveryAddress())
                        .status(order.getStatus())
                        .createdAt(order.getCreatedAt())
                        .build()
        ).collect(Collectors.toList());
    }

    // Next.js 관리자가 배송 상태 업데이트 시 반영
    public void updateDeliveryStatus(Long orderId, String status) {
        DeliveryOrder order = repository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("해당 주문 건이 존재하지 않습니다. ID: " + orderId));
        order.changeStatus(status);
    }
}