package com.busanit401.spring_back.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ProductApiController {

    // 1. 상품 목록 조회 API
    @GetMapping("/products")
    public ResponseEntity<List<Map<String, Object>>> getProductList() {
        List<Map<String, Object>> products = new ArrayList<>();

        Map<String, Object> p1 = new HashMap<>();
        p1.put("id", 101);
        p1.put("name", "프리미엄 바비큐 세트");
        p1.put("price", 45000);
        p1.put("description", "세컨하우스에서 즐기는 최고의 바비큐");
        products.add(p1);

        Map<String, Object> p2 = new HashMap<>();
        p2.put("id", 102);
        p2.put("name", "지역 특산물 밀키트");
        p2.put("price", 18000);
        p2.put("description", "신선한 현지 재료로 만든 간편 밀키트");
        products.add(p2);

        Map<String, Object> p3 = new HashMap<>();
        p3.put("id", 103);
        p3.put("name", "유기농 조식 바구니");
        p3.put("price", 25000);
        p3.put("description", "아침을 깨우는 건강한 유기농 식단");
        products.add(p3);

        Map<String, Object> p4 = new HashMap<>();
        p4.put("id", 104);
        p4.put("name", "감성 불멍 장작 세트");
        p4.put("price", 15000);
        p4.put("description", "따뜻한 캠핑 감성을 위한 오로라 장작");
        products.add(p4);

        return ResponseEntity.ok(products);
    }

    // 2. 장바구니 상품 추가 API
    @PostMapping("/cart/add")
    public ResponseEntity<Map<String, Object>> addToCart(@RequestBody Map<String, Object> request) {
        System.out.println("====== [장바구니 추가 요청 수신] ======");
        System.out.println("상품 ID (productId): " + request.get("productId"));
        System.out.println("추가 수량 (quantity): " + request.get("quantity"));

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success"); // 🟢 자바 Map 규격에 맞게 정교하게 수정
        response.put("message", "상품이 장바구니에 성공적으로 담겼습니다.");

        return ResponseEntity.ok(response);
    }
}