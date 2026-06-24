package com.busanit401.spring_back;

import com.busanit401.spring_back.domain.service.TourService;
import com.busanit401.spring_back.dto.TourDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest // 실제 스프링 부트 컨테이너 설정을 로드하여 외부 API 실통신 테스트 수행
class TourServiceTest {

    @Autowired
    private TourService tourService;

    @Test
    @DisplayName("지역 코드 26(부산)을 기반으로 공공 API에서 관광지 리스트 10개를 성공적으로 조회한다.")
    void getTourListByRegion_Busan_Success() {
        // given
        String regionCode = "26"; // 부산 지역 코드
        int targetPage = 1;       // 1페이지 요청

        // when
        // 포스트맨 규격(KorService2)을 사용하는 비즈니스 로직을 호출하여 실시간 데이터 수집
        List<TourDto> result = tourService.getTourListByRegion(regionCode, targetPage);

        // then
        // 1. 결과 리스트가 null이 아니어야 함
        assertThat(result).isNotNull();

        // 2. 포스트맨에서 검증된 API 구조이므로 정확히 10개의 데이터가 들어와야 함
        assertThat(result).hasSize(10);

        // 콘솔 출력 로그 확인용
        System.out.println("====== [지역 코드: " + regionCode + "] 관광지 10개 조회 완료 ======");
        System.out.println("가져온 관광지 총 개수: " + result.size());

        if (!result.isEmpty()) {
            System.out.println("첫 번째 관광지명: " + result.get(0).getTitle());
            System.out.println("첫 번째 관광지 주소: " + result.get(0).getAddr1());
            System.out.println("첫 번째 관광지 콘텐츠 ID: " + result.get(0).getContentid());
        }
    }
}