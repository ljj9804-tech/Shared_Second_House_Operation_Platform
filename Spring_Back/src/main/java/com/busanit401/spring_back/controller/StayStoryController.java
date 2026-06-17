//    실행 흐름:
//    프론트 요청 → Controller → Service → 응답
//    GET /api/stay/stories/{accommodationId} → 숙소별 스토리 목록
//    POST /api/stay/stories                  → 스토리 등록 (관리자)
//    PUT /api/stay/stories/{id}              → 스토리 수정 (관리자)
//    DELETE /api/stay/stories/{id}           → 스토리 삭제 (관리자)

package com.busanit401.spring_back.controller;

import com.busanit401.spring_back.domain.service.StayStoryService;
import com.busanit401.spring_back.dto.StayStoryRequestDto;
import com.busanit401.spring_back.dto.StayStoryResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RestController
@RequestMapping("/api/stay/stories")
@RequiredArgsConstructor
@Tag(name = "Stay Story", description = "숙소 스토리 관리 API")
public class StayStoryController {

    private final StayStoryService storyService;

    // 숙소별 스토리 목록 조회 (순서대로)
    @GetMapping("/{accommodationId}")
    @Operation(summary = "숙소별 스토리 목록 조회", description = "숙소 ID로 스토리 목록을 순서대로 조회합니다.")
    public ResponseEntity<List<StayStoryResponseDto>> getStories(@PathVariable Long accommodationId) {
        log.info("GET /api/stay/stories/{}", accommodationId);
        return ResponseEntity.ok(storyService.getStories(accommodationId));
    }

    // 스토리 등록 (관리자)
    @PostMapping
    @Operation(summary = "스토리 등록", description = "숙소 스토리를 등록합니다. (관리자)")
    public ResponseEntity<StayStoryResponseDto> createStory(@RequestBody StayStoryRequestDto requestDto) {
        log.info("POST /api/stay/stories - accommodationId: {}", requestDto.getAccommodationId());
        return ResponseEntity.ok(storyService.createStory(requestDto));
    }

    // 스토리 수정 (관리자)
    @PutMapping("/{id}")
    @Operation(summary = "스토리 수정", description = "숙소 스토리를 수정합니다. (관리자)")
    public ResponseEntity<StayStoryResponseDto> updateStory(
            @PathVariable Long id,
            @RequestBody StayStoryRequestDto requestDto) {
        log.info("PUT /api/stay/stories/{}", id);
        return ResponseEntity.ok(storyService.updateStory(id, requestDto));
    }

    // 스토리 삭제 (관리자)
    @DeleteMapping("/{id}")
    @Operation(summary = "스토리 삭제", description = "숙소 스토리를 삭제합니다. (관리자)")
    public ResponseEntity<Void> deleteStory(@PathVariable Long id) {
        log.info("DELETE /api/stay/stories/{}", id);
        storyService.deleteStory(id);
        return ResponseEntity.noContent().build();
    }
}