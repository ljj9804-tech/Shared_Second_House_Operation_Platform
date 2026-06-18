package com.busanit401.spring_back.controller;

import com.busanit401.spring_back.domain.service.PlacesService;
import com.busanit401.spring_back.dto.PlaceDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "주변 맛집 검색 (Google Places)", description = "좌표+반경으로 주변 맛집 조회")
@RestController
@RequestMapping("/api/places")
@RequiredArgsConstructor
public class PlacesController {

    private final PlacesService placesService;

    @GetMapping("/restaurants")
    @Operation(summary = "주변 맛집 검색 (좌표+반경)",
            description = "위도/경도 중심 radius(m) 반경의 맛집을 평점·관련도 순으로 반환. Google Places(New) Nearby Search.")
    public List<PlaceDTO> nearbyRestaurants(
            @Parameter(description = "위도", example = "35.1587")
            @RequestParam double lat,
            @Parameter(description = "경도", example = "129.1604")
            @RequestParam double lng,
            @Parameter(description = "반경(미터, 최대 50000)", example = "1000")
            @RequestParam(defaultValue = "1000") int radius,
            @Parameter(description = "결과 개수(최대 20)", example = "10")
            @RequestParam(defaultValue = "10") int limit) {

        return placesService.nearbyRestaurants(lat, lng, radius, limit);
    }
}