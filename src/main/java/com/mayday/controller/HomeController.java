package com.mayday.controller;

import com.mayday.domain.home.HomeDashboardService;
import com.mayday.domain.home.dto.HomeDashboardResponse;
import com.mayday.global.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/home")
public class HomeController {

    private final HomeDashboardService homeDashboardService;

    public HomeController(HomeDashboardService homeDashboardService) {
        this.homeDashboardService = homeDashboardService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<HomeDashboardResponse>> getHomeDashboard(
            @AuthenticationPrincipal Long userId
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok("홈 화면 데이터 조회 성공", homeDashboardService.getDashboard(userId))
        );
    }
}
