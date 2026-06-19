// 단위 테스트 시 DB에 더미 데이터 샘플로 넣고 진행하였음

package com.busanit401.spring_back;

import com.busanit401.spring_back.domain.service.TourService;
import com.busanit401.spring_back.dto.TourDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest // 실제 스프링 부트 컨테이너와 DB(데이터 소스) 설정을 통째로 로드
@Transactional  // 테스트 완료 후 DB에 영향을 주지 않도록 자동으로 롤백 처리
class TourServiceTest {

    @Autowired // 스프링이 관리하는 실제 TourService 빈 주입
    private TourService tourService;

    @Test
    @DisplayName("DB의 실제 숙소 데이터(강원도 속초)를 기반으로 공공 API 관광지 리스트를 조회한다.")
    void getNearbyTourList_RealDB_Sokcho_Success() {
        // given
        // 데이터 샘플 스크린샷 1번 라인: accommodation_id = 1 (속초 반달, 강원도 속초시)
        Long existingAccommodationId = 1L;

        // when
        // 가짜(Mock) 통신 없이 실제 DB에서 주소를 읽어와 오픈 API 서버와 직접 통신
        List<TourDto> result = tourService.getNearbyTourList(existingAccommodationId);

        // then
        // 가짜 데이터가 아니므로 실제 공공데이터포털에서 받아온 결과를 검증합니다.
        assertThat(result).isNotNull();

        // 데이터가 정상적으로 매핑되어 반환되었는지 확인하고 콘솔에 출력
        System.out.println("====== [속초 반달] 인근 관광지 조회 완료 ======");
        System.out.println("가져온 관광지 총 개수: " + result.size());

        if (!result.isEmpty()) {
            System.out.println("첫 번째 관광지명: " + result.get(0).getTitle());
            System.out.println("첫 번째 관광지 주소: " + result.get(0).getAddr1());
        }
    }

    @Test
    @DisplayName("DB의 실제 숙소 데이터(제주)를 기반으로 공공 API 관광지 리스트를 조회한다.")
    void getNearbyTourList_RealDB_Jeju_Success() {
        // given
        // 데이터 샘플 스크린샷 3번 라인: accommodation_id = 3 (제주 반달, 제주특별자치도)
        Long existingAccommodationId = 3L;

        // when
        List<TourDto> result = tourService.getNearbyTourList(existingAccommodationId);

        // then
        assertThat(result).isNotNull();

        System.out.println("====== [제주 반달] 인근 관광지 조회 완료 ======");
        System.out.println("가져온 관광지 총 개수: " + result.size());

        if (!result.isEmpty()) {
            System.out.println("첫 번째 관광지명: " + result.get(0).getTitle());
            System.out.println("첫 번째 관광지 주소: " + result.get(0).getAddr1());
        }
    }

    @Test
    @DisplayName("존재하지 않는 숙소 ID로 조회하는 경우 예외(IllegalArgumentException)가 발생한다.")
    void getNearbyTourList_NotFound_Exception() {
        // given
        // 샘플 데이터에 절대 존재하지 않는 임의의 큰 ID 값 지정
        Long invalidId = 9999L;

        // when & then
        // 서비스 단에서 "숙소 없음" 예외가 정상적으로 터지는지 검증
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> {
            System.out.println("존재하지 않는 숙소 ID");
            tourService.getNearbyTourList(invalidId);
        });
    }
}