package com.mayday.controller;

import com.mayday.domain.home.HomeSummaryService;
import com.mayday.domain.home.dto.HomeSummaryResponse;
import com.mayday.global.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/home")
public class HomeController {

    private final HomeSummaryService homeSummaryService;

    public HomeController(HomeSummaryService homeSummaryService) {
        this.homeSummaryService = homeSummaryService;
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<HomeSummaryResponse>> getHomeSummary(
            @AuthenticationPrincipal Long userId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok("홈 화면 데이터 조회 성공", homeSummaryService.getSummary(userId, year, month))
        );
    }
}
