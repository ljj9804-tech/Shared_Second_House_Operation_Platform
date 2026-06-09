package com.busanit401.spring_back.Controller;

import com.busanit401.spring_back.dto.DeliveryOrderDTO;
import com.busanit401.spring_back.service.DeliveryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Controller - 🛵 배달 주문 API", description = "플러터 앱 및 Next.js 관리자 웹 연동을 위한 주문·배송 관리 컨트롤러")
@RestController
@RequestMapping("/api/delivery")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // 프론트엔드 고유 포트(3000, 5000 등)와 연동을 위해 CORS 전면 허용
public class DeliveryController {

    private final DeliveryService deliveryService;

    /**
     * 📱 [POST] Flutter 앱의 결제완료 시 주문 접수 창구
     */
    @Operation(
            summary = "📱 [Flutter] 배달 주문 접수",
            description = "유저가 장바구니에서 결제를 완료하면 호출되는 API입니다. 전달받은 정보를 바탕으로 새로운 주문을 등록합니다."
    )
    @PostMapping("/orders")
    public ResponseEntity<Map<String, Object>> placeOrder(
            @RequestBody DeliveryOrderDTO dto) {

        Long orderId = deliveryService.createOrder(dto);

        // 프론트엔드에서 성공 여부와 생성된 주문 ID를 바로 꺼내 쓸 수 있도록 JSON 형태로 반환
        return ResponseEntity.ok(Map.of(
                "success", true,
                "orderId", orderId
        ));
    }

    /**
     * 💻 [GET] Next.js 관리자 페이지용 전체 주문 현황 리스트 반환
     */
    @Operation(
            summary = "💻 [Next.js] 관리자 전체 주문 조회",
            description = "관리자 웹 대시보드 화면에 뿌려줄 전체 배달 주문 목록을 시간 역순(최신순)으로 가져옵니다."
    )
    @GetMapping("/admin/orders")
    public ResponseEntity<List<DeliveryOrderDTO>> getAdminOrders() {

        List<DeliveryOrderDTO> orders = deliveryService.getAllOrders();

        return ResponseEntity.ok(orders);
    }

    /**
     * 💻 [PUT] Next.js 관리자 페이지에서 배송 상태(배송중/완료) 변경 제어
     */
    @Operation(
            summary = "💻 [Next.js] 관리자 배송 상태 수정",
            description = "특정 주문 건의 라이더 배정 상태 및 배송 현황 상태('배송중', '배송완료')를 업데이트합니다."
    )
    @PutMapping("/admin/orders/{orderId}")
    public ResponseEntity<Map<String, Object>> updateStatus(
            @Parameter(description = "상태를 변경할 주문의 고유 ID (order_id)", example = "1")
            @PathVariable Long orderId,
            @RequestBody Map<String, String> request) {

        // JSON Body 데이터 중 "status" 키값에 매핑된 밸류(예: "배송중")를 추출
        String status = request.get("status");

        deliveryService.updateDeliveryStatus(orderId, status);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "배송 상태가 [" + status + "](으)로 성공적으로 변경되었습니다."
        ));
    }
}