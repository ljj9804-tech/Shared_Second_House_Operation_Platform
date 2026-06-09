package com.busanit401.spring_back;

import com.busanit401.spring_back.Repository.DeliveryOrderRepository;
import com.busanit401.spring_back.domain.DeliveryOrder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@SpringBootTest
public class DeliveryRepositoryTests {

    @Autowired
    private DeliveryOrderRepository deliveryOrderRepository;

    @Test
    @DisplayName("배달 주문 생성 및 상태 변경 단위 테스트")
    @Transactional // 테스트가 끝난 후 DB 데이터를 깔끔하게 롤백(원상복구)해줍니다.
    public void testDeliveryOrderCrud() {

        // 1. Given (테스트할 가짜 데이터 준비)
        DeliveryOrder order = DeliveryOrder.builder()
                .totalAmount(25000)
                .deliveryAddress("부산광역시 해운대구 우동 세컨하우스 A동")
                .status("주문완료")
                .build();

        // 2. When (실제 레포지토리 기능 실행 - 저장)
        DeliveryOrder savedOrder = deliveryOrderRepository.save(order);

        // 3. Then (검증 - 저장이 정상적으로 잘 되었는지 확인)
        Assertions.assertNotNull(savedOrder.getOrderId(), "주문 번호(ID)가 생성되어야 합니다.");
        Assertions.assertEquals("주문완료", savedOrder.getStatus());

        // 4. Update When (배송 상태 변경 테스트 - 스웨거에서 했던 기능 검증)
        savedOrder.updateStatus("배송중"); // 엔티티 내부 상태 변경 메소드 호출
        DeliveryOrder updatedOrder = deliveryOrderRepository.save(savedOrder);

        // 5. Update Then (상태가 '배송중'으로 잘 바뀌었는지 최종 검증)
        Assertions.assertEquals("배송중", updatedOrder.getStatus(), "배송 상태가 '배송중'으로 변경되어야 합니다.");

        System.out.println("==================================================");
        System.out.println("🎉 단위 테스트 성공! 주문 번호: " + updatedOrder.getOrderId());
        System.out.println("==================================================");
    }
}