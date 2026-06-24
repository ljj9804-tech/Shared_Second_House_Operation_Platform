package com.busanit401.spring_back.controller;

import com.busanit401.spring_back.domain.service.TourService;
import com.busanit401.spring_back.dto.TourDto;
import io.jsonwebtoken.lang.Collections;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController // JSON 형태로 데이터를 반환하는 REST API 컨트롤러 선언
@RequestMapping("/api/tours") // 이 컨트롤러의 모든 API 기본 주소 경로 설정
@RequiredArgsConstructor
public class TourController {

    private final TourService tourService;

    // 파싱 편의를 위해 매개변수를 bodyMap으로 변경
    @SuppressWarnings("unchecked")
    private List<TourDto> parseTourDtoList(Map<String, Object> bodyMap) {
        try {
            if (bodyMap.get("items") == null || "".equals(bodyMap.get("items"))) {
                return Collections.emptyList();
            }

            Map<String, Object> itemsMap = (Map<String, Object>) bodyMap.get("items");
            Object itemObject = itemsMap.get("item");
            if (itemObject == null) return Collections.emptyList();

            List<Map<String, Object>> itemList = new ArrayList<>();

            if (itemObject instanceof List) {
                itemList = (List<Map<String, Object>>) itemObject;
            } else if (itemObject instanceof Map) {
                itemList.add((Map<String, Object>) itemObject);
            }

            return itemList.stream()
                    .map(item -> new TourDto(
                            item.get("title") != null ? String.valueOf(item.get("title")) : "정보 없음",
                            item.get("firstimage") != null ? String.valueOf(item.get("firstimage")) : "",
                            item.get("addr1") != null ? String.valueOf(item.get("addr1")) : "주소 정보 없음",
                            item.get("contentid") != null ? String.valueOf(item.get("contentid")) : ""
                            // 💡 외부 아이템에는 isLast가 없으므로 여기서 매핑하지 않음
                    ))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            System.err.println("▶ [JSON 파싱 중 에러 발생] 사유: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}

/*
 * 특정 지역 코드 기반으로 관광지 리스트를 10개씩 페이징 조회하는 API Endpoint
 * 예시 URL: GET http://localhost:8080/api/tours?lDongRegnCd=26&pageNo=1
 *
 * @param lDongRegnCd 클라이언트 카테고리 선택으로 들어올 법정동 시도 코드 (예: 부산=26, 인천=28)
 * @param pageNo      조회할 페이지 번호 (보내지 않을 경우 기본값 11페이지로 지정)
 * @return 200 OK 상태코드와 함께 10개의 정제된 관광지 DTO 리스트 반환
 */