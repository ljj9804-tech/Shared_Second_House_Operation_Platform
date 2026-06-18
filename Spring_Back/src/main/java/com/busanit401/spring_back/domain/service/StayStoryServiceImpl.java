//    실행 흐름:
//    Controller에서 호출 → Repository로 DB 조회/저장 → DTO로 변환 후 반환

package com.busanit401.spring_back.domain.service;

import com.busanit401.spring_back.domain.entity.StayAccommodation;
import com.busanit401.spring_back.domain.entity.StayStory;
import com.busanit401.spring_back.domain.repository.StayAccommodationRepository;
import com.busanit401.spring_back.domain.repository.StayStoryRepository;
import com.busanit401.spring_back.dto.StayStoryRequestDto;
import com.busanit401.spring_back.dto.StayStoryResponseDto;
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
public class StayStoryServiceImpl implements StayStoryService {

    private final StayStoryRepository storyRepository;
    private final StayAccommodationRepository accommodationRepository;

    // 숙소별 스토리 목록 조회 (순서대로)
    @Override
    public List<StayStoryResponseDto> getStories(Long accommodationId) {
        log.info("스토리 목록 조회 시작 - accommodationId: {}", accommodationId);
        List<StayStoryResponseDto> result = storyRepository.findByStayAccommodationIdOrderByOrderNumAsc(accommodationId).stream()
                .map(StayStoryResponseDto::from)
                .collect(Collectors.toList());
        log.info("스토리 목록 조회 완료 - 총 {}개", result.size());
        return result;
    }

    // 스토리 등록 (관리자)
    @Override
    @Transactional
    public StayStoryResponseDto createStory(StayStoryRequestDto requestDto) {
        log.info("스토리 등록 시작 - accommodationId: {}", requestDto.getAccommodationId());
        StayAccommodation accommodation = accommodationRepository.findById(requestDto.getAccommodationId())
                .orElseThrow(() -> new RuntimeException("숙소를 찾을 수 없습니다. id: " + requestDto.getAccommodationId()));

        StayStory story = StayStory.builder()
                .stayAccommodation(accommodation)
                .orderNum(requestDto.getOrderNum())
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .imageUrl(requestDto.getImageUrl())
                .build();

        StayStoryResponseDto result = StayStoryResponseDto.from(storyRepository.save(story));
        log.info("스토리 등록 완료 - id: {}", result.getId());
        return result;
    }

    // 스토리 수정 (관리자)
    @Override
    @Transactional
    public StayStoryResponseDto updateStory(Long id, StayStoryRequestDto requestDto) {
        log.info("스토리 수정 시작 - id: {}", id);
        StayStory story = storyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("스토리를 찾을 수 없습니다. id: " + id));

        story.update(requestDto);

        StayStoryResponseDto result = StayStoryResponseDto.from(storyRepository.save(story));
        log.info("스토리 수정 완료 - id: {}", id);
        return result;
    }

    // 스토리 삭제 (관리자)
    @Override
    @Transactional
    public void deleteStory(Long id) {
        log.info("스토리 삭제 시작 - id: {}", id);
        storyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("스토리를 찾을 수 없습니다. id: " + id));
        storyRepository.deleteById(id);
        log.info("스토리 삭제 완료 - id: {}", id);
    }
}

