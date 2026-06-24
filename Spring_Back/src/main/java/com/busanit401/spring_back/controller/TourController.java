package com.busanit401.spring_back.controller;

import com.busanit401.spring_back.domain.service.TourService;
import com.busanit401.spring_back.dto.TourDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController // JSON 형태로 데이터를 반환하는 REST API 컨트롤러 선언
@RequestMapping("/api/tours") // 이 컨트롤러의 모든 API 기본 주소 경로 설정
@RequiredArgsConstructor
public class TourController {

    private final TourService tourService;

    @GetMapping
    @Operation(summary = "지역별 카테고리 맞춤 관광지 리스트 10개씩 조회")
    public ResponseEntity<List<TourDto>> getToursByRegion(
            @Parameter(description = "법정동 시도 코드 (부산: 26, 인천: 28 등)", required = true)
            @RequestParam("lDongRegnCd") String lDongRegnCd,

            @Parameter(description = "요청할 페이지 번호 (기본값: 1)")
            @RequestParam(value = "pageNo", required = false, defaultValue = "1") int pageNo) {

        // 새로 변경된 서비스 레이어 호출 (지역 코드와 페이지 번호 매개변수 전달)
        List<TourDto> tourList = tourService.getTourListByRegion(lDongRegnCd, pageNo);

        // 프론트엔드에게 표준 HTTP 응답(200 OK)과 함께 리스트 전달
        return ResponseEntity.ok(tourList);
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