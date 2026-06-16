package com.busanit401.spring_back.domain.service;

import com.busanit401.spring_back.dto.StayStoryRequestDto;
import com.busanit401.spring_back.dto.StayStoryResponseDto;

import java.util.List;

public interface StayStoryService {

//    실행 흐름:
//    Controller → Service(interface) → ServiceImpl 순서로 호출

    // 숙소별 스토리 목록 조회 (순서대로)
    List<StayStoryResponseDto> getStories(Long accommodationId);

    // 스토리 등록 (관리자)
    StayStoryResponseDto createStory(StayStoryRequestDto requestDto);

    // 스토리 수정 (관리자)
    StayStoryResponseDto updateStory(Long id, StayStoryRequestDto requestDto);

    // 스토리 삭제 (관리자)
    void deleteStory(Long id);
}