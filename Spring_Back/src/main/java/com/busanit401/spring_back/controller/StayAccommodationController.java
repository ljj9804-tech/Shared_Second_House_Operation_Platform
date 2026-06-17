//    실행 흐름:
//    프론트 요청 → Controller → Service → 응답
//    GET /api/stay/accommodations        → 숙소 목록
//    GET /api/stay/accommodations/{id}   → 숙소 상세
//    POST /api/stay/accommodations       → 숙소 등록 (관리자)
//    PUT /api/stay/accommodations/{id}   → 숙소 수정 (관리자)
//    DELETE /api/stay/accommodations/{id} → 숙소 삭제 (관리자)

package com.busanit401.spring_back.controller;

import com.busanit401.spring_back.domain.service.StayAccommodationService;
import com.busanit401.spring_back.dto.StayAccommodationRequestDto;
import com.busanit401.spring_back.dto.StayAccommodationResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RestController
@RequestMapping("/api/stay/accommodations")
@RequiredArgsConstructor
@Tag(name = "Stay Accommodation", description = "숙소 관리 API")
public class StayAccommodationController {

    private final StayAccommodationService accommodationService;

    // 숙소 목록 조회
    @GetMapping
    @Operation(summary = "숙소 목록 조회", description = "전체 숙소 목록을 조회합니다.")
    public ResponseEntity<List<StayAccommodationResponseDto>> getAccommodations() {
        log.info("GET /api/stay/accommodations");
        return ResponseEntity.ok(accommodationService.getAccommodations());
    }

    // 숙소 상세 조회
    @GetMapping("/{id}")
    @Operation(summary = "숙소 상세 조회", description = "숙소 ID로 상세 정보를 조회합니다.")
    public ResponseEntity<StayAccommodationResponseDto> getAccommodation(@PathVariable Long id) {
        log.info("GET /api/stay/accommodations/{}", id);
        return ResponseEntity.ok(accommodationService.getAccommodation(id));
    }

    // 숙소 등록 (관리자)
    @PostMapping
    @Operation(summary = "숙소 등록", description = "새로운 숙소를 등록합니다. (관리자)")
    public ResponseEntity<StayAccommodationResponseDto> createAccommodation(
            @RequestBody StayAccommodationRequestDto requestDto) {
        log.info("POST /api/stay/accommodations - name: {}", requestDto.getName());
        return ResponseEntity.ok(accommodationService.createAccommodation(requestDto));
    }

    // 숙소 수정 (관리자)
    @PutMapping("/{id}")
    @Operation(summary = "숙소 수정", description = "숙소 정보를 수정합니다. (관리자)")
    public ResponseEntity<StayAccommodationResponseDto> updateAccommodation(
            @PathVariable Long id,
            @RequestBody StayAccommodationRequestDto requestDto) {
        log.info("PUT /api/stay/accommodations/{}", id);
        return ResponseEntity.ok(accommodationService.updateAccommodation(id, requestDto));
    }

    // 숙소 삭제 (관리자)
    @DeleteMapping("/{id}")
    @Operation(summary = "숙소 삭제", description = "숙소를 삭제합니다. (관리자)")
    public ResponseEntity<Void> deleteAccommodation(@PathVariable Long id) {
        log.info("DELETE /api/stay/accommodations/{}", id);
        accommodationService.deleteAccommodation(id);
        return ResponseEntity.noContent().build();
    }
}