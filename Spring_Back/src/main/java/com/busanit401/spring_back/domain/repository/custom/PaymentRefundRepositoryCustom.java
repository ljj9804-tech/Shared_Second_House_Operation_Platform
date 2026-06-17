package com.busanit401.spring_back.domain.repository.custom;

import com.busanit401.spring_back.domain.PaymentRefund;
import com.busanit401.spring_back.dto.PaymentRefund.PaymentRefundSearchCondition;
import java.util.List;

public interface PaymentRefundRepositoryCustom {

    // 관리자 페이지 복합 조건 검색
    List<PaymentRefund> searchByCondition(PaymentRefundSearchCondition condition);
}
