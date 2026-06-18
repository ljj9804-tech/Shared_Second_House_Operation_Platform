package com.busanit401.spring_back.domain.service;

import com.busanit401.spring_back.domain.repository.StayAccommodationRepository;
import com.busanit401.spring_back.dto.TourDto;
import com.busanit401.spring_back.enums.TourAddress;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TourService {

    private final StayAccommodationRepository stayAccommodationRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${tour.api.service-key}")
    private String serviceKey;

    public List<TourDto> getNearbyTourList(Long accommodationId) {
         var accommodation = stayAccommodationRepository.findById(accommodationId)
                .orElseThrow(() -> new IllegalArgumentException("숙소 없음"));
        String lDongRegnCd = TourAddress.getRegionCode(accommodation.getAddress());

        try {
            StringBuilder urlBuilder = new StringBuilder("https://apis.data.go.kr/B551011/KorService1/areaBasedList1");
            urlBuilder.append("?serviceKey=").append(serviceKey);
            urlBuilder.append("&numOfRows=").append(10);
            urlBuilder.append("&pageNo=").append(1);
            urlBuilder.append("&MobileOS=").append("ETC");
            urlBuilder.append("&MobileApp=").append("AppTest");
            urlBuilder.append("&_type=").append("json");
            urlBuilder.append("&arrange=").append("C");
            urlBuilder.append("&contentTypeId=").append("12");
            urlBuilder.append("&lclsSystm1=").append("NA");
            urlBuilder.append("&lclsSystm2=").append("NA04");
            urlBuilder.append("&lclsSystm3=").append("NA040500");
            urlBuilder.append("&lDongRegnCd=").append(lDongRegnCd);

            URI targetUri = new URI(urlBuilder.toString());
            Map<String, Object> response = restTemplate.getForObject(targetUri, Map.class);

            return parseTourDtoList(response);

        } catch (Exception e) {
            System.err.println("관광공사 API 데이터 처리 중 오류 발생: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    @SuppressWarnings("unchecked")
    private List<TourDto> parseTourDtoList(Map<String, Object> response) {
        try {
            if (response == null) return Collections.emptyList();

            Map<String, Object> resMap = (Map<String, Object>) response.get("response");
            Map<String, Object> bodyMap = (Map<String, Object>) resMap.get("body");
            Map<String, Object> itemsMap = (Map<String, Object>) bodyMap.get("items");
            List<Map<String, Object>> itemList = (List<Map<String, Object>>) itemsMap.get("item");

            if (itemList == null) return Collections.emptyList();

            // 각 요소를 TourDto로 맵핑
            return itemList.stream()
                    .map(item -> new TourDto(
                            (String) item.get("title"),
                            (String) item.get("firstimage"),
                            (String) item.get("addr1"),
                            (String) item.get("contentid")
                    ))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            // JSON 구조가 매핑되지 않거나 데이터가 없을 때의 예외 처리
            return Collections.emptyList();
        }
    }
}