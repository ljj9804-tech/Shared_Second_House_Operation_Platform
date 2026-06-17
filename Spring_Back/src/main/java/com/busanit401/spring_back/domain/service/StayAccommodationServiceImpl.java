//    실행 흐름:
//    Controller에서 호출 → Repository로 DB 조회/저장 → DTO로 변환 후 반환

package com.busanit401.spring_back.domain.service;

import com.busanit401.spring_back.domain.entity.StayAccommodation;
import com.busanit401.spring_back.domain.repository.StayAccommodationPriceRepository;
import com.busanit401.spring_back.domain.repository.StayAccommodationRepository;
import com.busanit401.spring_back.dto.StayAccommodationPriceDto;
import com.busanit401.spring_back.dto.StayAccommodationRequestDto;
import com.busanit401.spring_back.dto.StayAccommodationResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StayAccommodationServiceImpl implements StayAccommodationService {

    private final StayAccommodationRepository accommodationRepository;
    private final StayAccommodationPriceRepository priceRepository;

    // 공통 할인율 구간 목록 조회
    private List<StayAccommodationPriceDto> getCommonPrices() {
        return priceRepository.findAll().stream()
                .map(StayAccommodationPriceDto::from)
                .collect(Collectors.toList());
    }

    // 숙소 목록 조회
    @Override
    public List<StayAccommodationResponseDto> getAccommodations() {
        log.info("숙소 목록 조회 시작");
        List<StayAccommodationPriceDto> prices = getCommonPrices();
        List<StayAccommodationResponseDto> result = accommodationRepository.findAll().stream()
                .map(accommodation -> StayAccommodationResponseDto.from(accommodation, prices))
                .collect(Collectors.toList());
        log.info("숙소 목록 조회 완료 - 총 {}개", result.size());
        return result;
    }

    // 숙소 상세 조회
    @Override
    public StayAccommodationResponseDto getAccommodation(Long id) {
        log.info("숙소 상세 조회 시작 - id: {}", id);
        StayAccommodation accommodation = accommodationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("숙소를 찾을 수 없습니다. id: " + id));
        List<StayAccommodationPriceDto> prices = getCommonPrices();
        log.info("숙소 상세 조회 완료 - name: {}", accommodation.getName());
        return StayAccommodationResponseDto.from(accommodation, prices);
    }

    // 숙소 등록 (관리자)
    @Override
    @Transactional
    public StayAccommodationResponseDto createAccommodation(StayAccommodationRequestDto requestDto) {
        log.info("숙소 등록 시작 - name: {}", requestDto.getName());
        StayAccommodation accommodation = StayAccommodation.builder()
                .name(requestDto.getName())
                .address(requestDto.getAddress())
                .description(requestDto.getDescription())
                .imageUrl(requestDto.getImageUrl())
                .amenities(requestDto.getAmenities())
                .monthlyPrice(requestDto.getMonthlyPrice())
                .roomCount(requestDto.getRoomCount())
                .bathroomCount(requestDto.getBathroomCount())
                .floorCount(requestDto.getFloorCount())
                .parkingCount(requestDto.getParkingCount())
                .landArea(requestDto.getLandArea())
                .buildingArea(requestDto.getBuildingArea())
                .latitude(requestDto.getLatitude())
                .longitude(requestDto.getLongitude())
                .status(requestDto.getStatus())
                .build();
        StayAccommodation saved = accommodationRepository.save(accommodation);
        List<StayAccommodationPriceDto> prices = getCommonPrices();
        log.info("숙소 등록 완료 - id: {}", saved.getId());
        return StayAccommodationResponseDto.from(saved, prices);
    }

    // 숙소 수정 (관리자)
    @Override
    @Transactional
    public StayAccommodationResponseDto updateAccommodation(Long id, StayAccommodationRequestDto requestDto) {
        log.info("숙소 수정 시작 - id: {}", id);
        StayAccommodation accommodation = accommodationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("숙소를 찾을 수 없습니다. id: " + id));
        accommodation.update(requestDto);
        StayAccommodation saved = accommodationRepository.save(accommodation);
        List<StayAccommodationPriceDto> prices = getCommonPrices();
        log.info("숙소 수정 완료 - id: {}", id);
        return StayAccommodationResponseDto.from(saved, prices);
    }

    // 숙소 삭제 (관리자)
    @Override
    @Transactional
    public void deleteAccommodation(Long id) {
        log.info("숙소 삭제 시작 - id: {}", id);
        accommodationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("숙소를 찾을 수 없습니다. id: " + id));
        accommodationRepository.deleteById(id);
        log.info("숙소 삭제 완료 - id: {}", id);
    }
}