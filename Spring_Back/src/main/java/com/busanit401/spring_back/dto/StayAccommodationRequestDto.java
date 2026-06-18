package com.busanit401.spring_back.dto;

import com.busanit401.spring_back.enums.StayAccommodationStatus;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StayAccommodationRequestDto {

//    실행 흐름:
//    관리자가 숙소 등록/수정 시 → 프론트에서 이 DTO로 데이터 전송
//    Controller에서 받아서 → Service에서 Entity로 변환

    private String name;            // 숙소명
    private String address;         // 주소
    private String description;     // 숙소 설명
    private String imageUrl;        // 이미지 URL 목록 (쉼표 구분)
    private String amenities;       // 구성용품 (쉼표 구분)

    private Integer monthlyPrice;   // 숙소별 월세

    private Integer roomCount;      // 방 수
    private Integer bathroomCount;  // 화장실 수
    private Integer floorCount;     // 층수
    private Integer parkingCount;   // 주차 가능 대수

    private Double landArea;        // 대지면적 (평)
    private Double buildingArea;    // 건물면적 (평)

    private Double latitude;        // 위도
    private Double longitude;       // 경도

    private StayAccommodationStatus status; // AVAILABLE / MAINTENANCE
}