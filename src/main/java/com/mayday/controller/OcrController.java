package com.mayday.controller;

import com.mayday.domain.expense.OcrService;
import com.mayday.domain.expense.dto.OcrResultData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/expenses")
public class OcrController {

    private final OcrService ocrService;

    @PostMapping(value = "/ocr", consumes = "multipart/form-data")
    public ResponseEntity<?> extractOcr(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal Long userId
    ) {
        OcrResultData result = ocrService.extract(file, userId);
        return ResponseEntity.ok(result);
    }
}
