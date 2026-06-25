package com.busanit401.spring_back.domain.service;

import com.busanit401.spring_back.domain.CartItemRequest;
import com.busanit401.spring_back.domain.OrderRequest;
import com.busanit401.spring_back.domain.Order;
import com.busanit401.spring_back.domain.OrderItem;
import com.busanit401.spring_back.domain.Product;
import com.busanit401.spring_back.domain.repository.CartRepository;
import com.busanit401.spring_back.domain.repository.OrderItemRepository;
import com.busanit401.spring_back.domain.repository.OrderRepository;
import com.busanit401.spring_back.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;

    // 1. 주문 생성 로직 (플러터 연동)
    public Long createOrder(OrderRequest dto) {
        // 마스터 주문 저장 (sh_order)
        Order order = Order.builder()
                .userId(dto.getUserId())
                .deliveryAddress(dto.getDeliveryAddress())
                .totalAmount(dto.getTotalAmount())
                .status("주문대기")
                .build();

        Order savedOrder = orderRepository.save(order);

        // 상세 아이템 목록 추가 (sh_order_item)
        for (CartItemRequest itemDto : dto.getItems()) {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품 ID입니다: " + itemDto.getProductId()));

            OrderItem orderItem = OrderItem.builder()
                    .order(savedOrder)
                    .product(product)
                    .quantity(itemDto.getQuantity())
                    .price(itemDto.getPrice())
                    .build();

            orderItemRepository.save(orderItem);
        }

        // 주문 완료 후 장바구니 비우기 (선택 사항)
        cartRepository.deleteByUserId(dto.getUserId());

        return savedOrder.getOrderId();
    }

    // 2. 전체 주문 조회 로직 (리액트 어드민 연동)
    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}
