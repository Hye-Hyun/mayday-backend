package com.mayday.controller;

import com.mayday.domain.expense.OcrService;
import com.mayday.domain.expense.dto.OcrResultData;
import com.mayday.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/expenses")
@Slf4j
public class OcrController {

    private final OcrService ocrService;

    @PostMapping(value = "/ocr", consumes = "multipart/form-data")
    public ResponseEntity<?> extractOcr(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal Long userId
    ) {
        log.info("OCR request reached controller. userId={}, fileName={}, contentType={}, size={}",
                userId,
                file.getOriginalFilename(),
                file.getContentType(),
                file.getSize());
        OcrResultData result = ocrService.extract(file, userId);
        return ResponseEntity.ok(ApiResponse.ok("OCR 추출 성공", result));
    }
}
