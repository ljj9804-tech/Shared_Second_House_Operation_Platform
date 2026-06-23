package com.busanit401.spring_back.controller;

import com.busanit401.spring_back.domain.service.TourService;
import com.busanit401.spring_back.dto.TourDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController // JSON 형태로 데이터를 반환하는 REST API 컨트롤러 선언
@RequestMapping("/api/tours") // 이 컨트롤러의 모든 API 기본 주소 경로 설정
@RequiredArgsConstructor
public class TourController {

    private final TourService tourService;

    /**
     * 특정 숙소 근처의 관광지 리스트를 조회하는 API Endpoint
     * GET http://localhost:8080/api/tours/nearby/1
     *
     * @param accommodationId URL 경로로 전달받는 숙소의 식별자(ID)
     * @return 200 OK 상태코드와 함께 정제된 관광지 DTO 리스트 반환
     */
    @GetMapping("/nearby/{accommodationId}")
    public ResponseEntity<List<TourDto>> getNearbyTours(@PathVariable("accommodationId") Long accommodationId) {

        // 서비스 레이어를 호출하여 데이터 가져오기
        List<TourDto> tourList = tourService.getNearbyTourList(accommodationId);

        // 프론트엔드에게 표준 HTTP 응답(200 OK)과 함께 리스트 전달
        return ResponseEntity.ok(tourList);
    }
}
