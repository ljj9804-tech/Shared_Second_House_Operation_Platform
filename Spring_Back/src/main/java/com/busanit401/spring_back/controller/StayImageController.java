//    실행 흐름:
//    프론트(관리자)에서 이미지 파일 업로드 → 서버 /uploads 폴더에 저장 → 저장된 URL 반환
//    반환된 URL → StayAccommodation.imageUrl 또는 StayStory.imageUrl에 저장
//    POST /api/stay/accommodations/{id}/images → 숙소 이미지 업로드

package com.busanit401.spring_back.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Log4j2
@RestController
@RequestMapping("/api/stay/accommodations")
@RequiredArgsConstructor
@Tag(name = "Stay Image", description = "숙소 이미지 업로드 API")
public class StayImageController {

    // 이미지 저장 경로 (서버 로컬)
    // TODO [고도화]: 이미지 파일 업로드 → S3 연동으로 교체
    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";

    // 숙소 이미지 업로드 (여러 장)
    @PostMapping("/{id}/images")
    @Operation(summary = "숙소 이미지 업로드", description = "숙소 이미지를 업로드하고 URL 목록을 반환합니다.")
    public ResponseEntity<List<String>> uploadImages(
            @PathVariable Long id,
            @RequestParam("files") List<MultipartFile> files) throws IOException {

        log.info("POST /api/stay/accommodations/{}/images - 파일 수: {}", id, files.size());

        // 업로드 폴더 없으면 생성
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        List<String> imageUrls = new ArrayList<>();

        for (MultipartFile file : files) {
            // UUID로 파일명 중복 방지
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            File dest = new File(UPLOAD_DIR + fileName);
            file.transferTo(dest);

            // 프론트에서 접근 가능한 URL 형태로 반환
            imageUrls.add("/uploads/" + fileName);
            log.info("이미지 업로드 완료 - fileName: {}", fileName);
        }

        log.info("전체 이미지 업로드 완료 - 총 {}개", imageUrls.size());
        return ResponseEntity.ok(imageUrls);
    }
}