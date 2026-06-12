package com.busanit401.spring_back.domain.repository.custom;

import com.busanit401.spring_back.domain.Payment;
import com.busanit401.spring_back.dto.PaymentSearchCondition;
import java.util.List;

public interface PaymentRepositoryCustom {

    // 관리자 페이지 복합 조건 검색
    List<Payment> searchByCondition(PaymentSearchCondition condition);
}
