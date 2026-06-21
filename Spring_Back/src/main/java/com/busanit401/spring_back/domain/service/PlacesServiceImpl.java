package com.busanit401.spring_back.domain.service;

import com.busanit401.spring_back.domain.service.places.GooglePlacesClient;
import com.busanit401.spring_back.dto.PlaceDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * {@link PlacesService} 구현. Google Places(New) 호출을 {@link GooglePlacesClient}에 위임하고,
 * 여기서는 Places API 제약에 맞게 입력값을 보정(클램핑)한다.
 */
@Service
@RequiredArgsConstructor
public class PlacesServiceImpl implements PlacesService {

    /** Places 제약: 반경 0~50000m. */
    private static final int MAX_RADIUS = 50_000;
    /** Places 제약: 결과 1~20개. */
    private static final int MAX_LIMIT = 20;
    /** 결과 언어. */
    private static final String LANG = "ko";

    private final GooglePlacesClient googlePlacesClient;

    /** 정상 영업 상태 값(폐업·임시휴업 제외). */
    private static final String STATUS_OPERATIONAL = "OPERATIONAL";

    @Override
    public List<PlaceDTO> nearbyRestaurants(double latitude, double longitude, int radius, int limit) {
        int safeRadius = clamp(radius, 1, MAX_RADIUS);
        int safeLimit = clamp(limit, 1, MAX_LIMIT);
        // 영업중(OPERATIONAL)만 추리면 개수가 줄 수 있어, 최대치로 받아온 뒤 필터 → safeLimit개로 자른다.
        // (Nearby Search는 요청당 과금이라 maxResultCount를 늘려도 비용 동일. 인기도순은 그대로 유지됨)
        return googlePlacesClient.searchNearbyRestaurants(latitude, longitude, safeRadius, MAX_LIMIT, LANG)
                .stream()
                .filter(p -> STATUS_OPERATIONAL.equals(p.getBusinessStatus()))
                .limit(safeLimit)
                .toList();
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}