package com.busanit401.spring_back.dto;

import com.busanit401.spring_back.domain.entity.StayAccommodation;
import com.busanit401.spring_back.enums.StayAccommodationStatus;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StayAccommodationResponseDto {

//    실행 흐름:
//    Service에서 Entity 조회 → 이 DTO로 변환 → Controller에서 프론트로 응답

    private Long id;                // 숙소 ID
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

    private List<StayAccommodationPriceDto> prices; // 공통 할인율 구간 목록

    // Entity → DTO 변환 메서드
    public static StayAccommodationResponseDto from(StayAccommodation accommodation, List<StayAccommodationPriceDto> prices) {
        return StayAccommodationResponseDto.builder()
                .id(accommodation.getId())
                .name(accommodation.getName())
                .address(accommodation.getAddress())
                .description(accommodation.getDescription())
                .imageUrl(accommodation.getImageUrl())
                .amenities(accommodation.getAmenities())
                .monthlyPrice(accommodation.getMonthlyPrice())
                .roomCount(accommodation.getRoomCount())
                .bathroomCount(accommodation.getBathroomCount())
                .floorCount(accommodation.getFloorCount())
                .parkingCount(accommodation.getParkingCount())
                .landArea(accommodation.getLandArea())
                .buildingArea(accommodation.getBuildingArea())
                .latitude(accommodation.getLatitude())
                .longitude(accommodation.getLongitude())
                .status(accommodation.getStatus())
                .prices(prices)
                .build();
    }
}