package com.busanit401.spring_back.domain.service;

import com.busanit401.spring_back.dto.PlaceDTO;

import java.util.List;

/**
 * 주변 맛집 검색 서비스의 공개 계약. 컨트롤러는 이 인터페이스에만 의존하고,
 * Google Places 호출·기본값 보정 등은 구현체가 담당한다.
 */
public interface PlacesService {

    /**
     * 좌표(lat,lng) 중심 radius(m) 반경의 맛집을 최대 limit개 반환.
     * @param radius 반경(미터)
     * @param limit  최대 결과 수
     */
    List<PlaceDTO> nearbyRestaurants(double latitude, double longitude, int radius, int limit);
}