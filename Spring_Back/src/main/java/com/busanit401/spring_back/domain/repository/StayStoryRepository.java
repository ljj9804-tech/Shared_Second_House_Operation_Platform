package com.busanit401.spring_back.domain.repository;

import com.busanit401.spring_back.domain.entity.StayStory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StayStoryRepository extends JpaRepository<StayStory, Long> {

    // 숙소별 스토리 목록 조회 (순서대로)
    List<StayStory> findByStayAccommodationIdOrderByOrderNumAsc(Long accommodationId);
}