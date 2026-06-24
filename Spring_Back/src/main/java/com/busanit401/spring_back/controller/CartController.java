package com.busanit401.spring_back.controller;

import com.busanit401.spring_back.domain.Cart;
import com.busanit401.spring_back.domain.service.CartService;
import com.busanit401.spring_back.dto.CartResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CartController {

    private final CartService cartService;

    @GetMapping("/{userId}")
    public ResponseEntity<List<CartResponseDto>> getCartItems(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(cartService.getCartList(userId));
    }

    @PostMapping("/add")
    public ResponseEntity<String> addToCart(@RequestBody Cart request) {
        // userId 1004는 테스트용 고정값, 추후 세션/토큰에서 가져오세요
        cartService.addCart(1004L, request.getProduct().getProductId(), request.getQuantity());
        return ResponseEntity.ok("장바구니 담기 성공");
    }
}