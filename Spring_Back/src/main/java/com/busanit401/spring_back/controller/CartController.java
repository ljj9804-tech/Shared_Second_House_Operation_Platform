package com.busanit401.spring_back.controller;

import com.busanit401.spring_back.domain.Cart;
import com.busanit401.spring_back.domain.repository.CartRepository; // 팀의 실제 Repository 경로로 맞춰주세요!
import com.busanit401.spring_back.dto.CartResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // 💡 플러터와의 통신(CORS) 에러 방지
public class CartController {

    private final CartRepository cartRepository;

    /**
     * 🛒 특정 유저의 장바구니 목록 실시간 조회
     * GET http://localhost:8080/api/cart/{userId}
     */
    @GetMapping("/{userId}")
    public ResponseEntity<List<CartResponseDto>> getCartItems(@PathVariable("userId") Long userId) {

        // 1. DB에서 해당 유저의 장바구니 내역을 전부 조회
        List<Cart> cartList = cartRepository.findByUserId(userId);

        // 2. 💡 [500 에러 해결 핵심] 엔티티를 그대로 반환하지 않고 순수 JSON DTO 구조로 변환!
        List<CartResponseDto> dtoList = cartList.stream().map(cart -> {
            CartResponseDto dto = new CartResponseDto();
            dto.setCartId(cart.getCartId());
            dto.setUserId(cart.getUserId());

            // Cart 엔티티 내부의 Product 연관 객체에서 안전하게 필드 추출
            if (cart.getProduct() != null) {
                dto.setProductId(cart.getProduct().getProductId()); // 상품 ID
                dto.setName(cart.getProduct().getName());           // 상품 이름
                dto.setPrice(cart.getProduct().getPrice());         // 상품 단가
            }

            dto.setQuantity(cart.getQuantity()); // 담은 수량
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(dtoList);
    }

}