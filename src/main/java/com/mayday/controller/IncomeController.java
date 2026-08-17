package com.mayday.controller;

import com.mayday.domain.income.IncomeService;
import com.mayday.domain.income.dto.IncomeProgressResponse;
import com.mayday.global.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.mayday.domain.income.dto.IncomeCreateRequest;
import com.mayday.domain.income.dto.IncomeCreateResponse;
import com.mayday.domain.income.dto.IncomeListResponse;
import com.mayday.domain.income.dto.IncomeUpdateRequest;
import com.mayday.domain.income.dto.IncomeUpdateResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/incomes")
public class IncomeController {

    private final IncomeService incomeService;

    public IncomeController(IncomeService incomeService) {
        this.incomeService = incomeService;
    }

    @GetMapping("/progress")
    public ResponseEntity<ApiResponse<IncomeProgressResponse>> getProgress(
            @AuthenticationPrincipal Long userId
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok("수입 현황 조회 성공", incomeService.getProgress(userId))
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<IncomeCreateResponse>> createIncome(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody IncomeCreateRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created("부수입 저장 성공", incomeService.create(userId, request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<IncomeListResponse>> getIncomes(
            @AuthenticationPrincipal Long userId,
            @RequestParam(required = false) Integer year
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok("연도별 부수입 목록 조회 성공", incomeService.getListByYear(userId, year))
        );
    }

    @PatchMapping("/{incomeId}")
    public ResponseEntity<ApiResponse<IncomeUpdateResponse>> updateIncome(
            @AuthenticationPrincipal Long userId,
            @PathVariable String incomeId,
            @Valid @RequestBody IncomeUpdateRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok("부수입 수정 성공", incomeService.update(userId, incomeId, request))
        );
    }

    @DeleteMapping("/{incomeId}")
    public ResponseEntity<ApiResponse<Void>> deleteIncome(
            @AuthenticationPrincipal Long userId,
            @PathVariable String incomeId
    ) {
        incomeService.delete(userId, incomeId);
        return ResponseEntity.ok(ApiResponse.ok("부수입 삭제 성공", null));
    }
}