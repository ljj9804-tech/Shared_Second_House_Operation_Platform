package com.busanit401.spring_back.domain.repository;

import com.busanit401.spring_back.domain.DeliveryOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DeliveryOrderRepository extends JpaRepository<DeliveryOrder, Long> {
    // 유저별 최신 주문 내역 조회 레코드 (추후 내비게이션이나 마이페이지 연동용)
    List<DeliveryOrder> findByUserIdOrderByCreatedAtDesc(Long userId);
}
