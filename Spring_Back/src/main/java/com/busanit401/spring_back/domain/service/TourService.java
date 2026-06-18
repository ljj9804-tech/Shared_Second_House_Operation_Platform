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

    // 숙소 데이터를 조회
    private final StayAccommodationRepository stayAccommodationRepository;

    // 외부 API와 HTTP 통신을 수행하기 위한 스프링 내장 객체 초기화
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${tour.api.service-key}")
    private String serviceKey;

    //특정 숙소의 주소 기반으로 인근의 관광지 목록을 조회하는 메인 비즈니스 메서드
    public List<TourDto> getNearbyTourList(Long accommodationId) {
         // 1. 전달받은 숙소 ID로 DB에서 숙소 정보를 조회 (없으면 예외 발생)
         var accommodation = stayAccommodationRepository.findById(accommodationId)
                .orElseThrow(() -> new IllegalArgumentException("숙소 없음"));
        // 2. 조회한 숙소의 주소 텍스트를 전용 Enum(TourAddress)에 넘겨 법정동 시도 코드(2자리) 추출
        String lDongRegnCd = TourAddress.getRegionCode(accommodation.getAddress());

        try {
            // 3. 한국관광공사 Open API 호출을 위한 주소(URL) 및 쿼리 파라미터 동적 조립
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
            urlBuilder.append("&lDongRegnCd=").append(lDongRegnCd); // 추출한 시도 코드

            // 4. 공공데이터 특유의 인코딩 깨짐(Double Encoding)을 방지하기 위해 자바 URI 객체 생성
            URI targetUri = new URI(urlBuilder.toString());

            // 5. 생성한 URI를 기반으로 외부 API 서버에 GET 요청을 보내고 결과를 계층형 Map 구조로 받아옴
            Map<String, Object> response = restTemplate.getForObject(targetUri, Map.class);

            // 6. TourDto 리스트로 가공 후 리턴
            return parseTourDtoList(response);

        } catch (Exception e) {
            System.err.println("관광공사 API 데이터 처리 중 오류 발생: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    // 외부 API가 리턴한 복잡한 JSON(Map) 구조에서 핵심 알맹이만 꺼내 자바 객체(TourDto)로 변환하는 헬퍼 메서드
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


/*
 * [파일 역할 설명]
 * 사용자가 선택한 특정 숙소 주변의 맞춤형 관광지 데이터를 제공하기 위한 비즈니스 로직 처리 파일입니다.
 * 1. 데이터 베이스와의 상호작용을 통해 선택된 숙소의 실제 주소 데이터를 조회합니다.
 * 2. 내부 주소 변환 알고리즘(TourAddress Enum)을 구동해 주소를 공공데이터 표준 행정구역 코드로 치환합니다.
 * 3. 공공데이터포털(한국관광공사)의 국문 관광정보 오픈 API 서버와 실시간으로 원격 HTTP 통신(RestTemplate)을 수행합니다.
 * 4. 공공 서버에서 받은 복잡한 형태의 JSON 원본 데이터를 서비스 규격에 맞는 프론트엔드 전용 규격(TourDto)으로 정제 및 가공하는 역할을 담당합니다.
 */