package com.mayday.controller;

import com.mayday.domain.mypage.MyPageService;
import com.mayday.domain.mypage.dto.MyPageSummaryResponse;
import com.mayday.global.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users/me")
public class MyPageController {

    private final MyPageService myPageService;

    public MyPageController(MyPageService myPageService) {
        this.myPageService = myPageService;
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<MyPageSummaryResponse>> getMyPageSummary(
            @AuthenticationPrincipal Long userId,
            @RequestParam(required = false) Integer year
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok("마이페이지 요약 정보 조회 성공", myPageService.getSummary(userId, year))
        );
    }
}
