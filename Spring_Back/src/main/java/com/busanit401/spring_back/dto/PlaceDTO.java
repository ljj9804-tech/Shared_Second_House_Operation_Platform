package com.busanit401.spring_back.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Google Places 검색 결과 1건(맛집 한 곳). 비용 절감을 위해 <b>Pro 등급 필드만</b> 담는다.
 * (평점·가격·전화·영업시간 등 Enterprise/Atmosphere 필드는 제외 — 상세는 googleMapsUri로 연결)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceDTO {

    // --- 기본 정보 ---
    /** Places 고유 id (place id). 상세조회 등에 사용 가능. */
    private String id;
    /** 가게 이름 (displayName.text). */
    private String name;
    /** 도로명/지번 주소 (formattedAddress). */
    private String address;
    /** 위도 (location.latitude). */
    private Double latitude;
    /** 경도 (location.longitude). */
    private Double longitude;

    // --- 음식점 종류 ---
    /** 대표 타입 코드 (primaryType, 예: "japanese_restaurant"). */
    private String primaryType;
    /** 대표 타입 표시명 (primaryTypeDisplayName.text, 예: "일식당"). */
    private String primaryTypeName;
    /** 전체 타입 코드 목록 (types, 예: ["japanese_restaurant","restaurant","food"]). */
    private List<String> types;

    // --- 상태/링크 ---
    /** 영업 상태 (businessStatus, 예: OPERATIONAL/CLOSED_TEMPORARILY). */
    private String businessStatus;
    /** 구글 지도 링크 (googleMapsUri) — 평점·영업시간 등 상세는 이 링크로 연결. */
    private String googleMapsUri;
}