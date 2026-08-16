package com.mayday.controller;

import com.mayday.domain.expense.ExpenseAnalyzeService;
import com.mayday.domain.expense.dto.ExpenseAnalyzeRequest;
import com.mayday.domain.expense.dto.ExpenseAnalyzeResponse;
import com.mayday.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/expenses")
public class ExpenseAnalyzeController {

    private final ExpenseAnalyzeService expenseAnalyzeService;

    @PostMapping("/analyze")
    public ResponseEntity<ApiResponse<ExpenseAnalyzeResponse>> analyze(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody ExpenseAnalyzeRequest request
    ) {
        ExpenseAnalyzeResponse response = expenseAnalyzeService.analyze(request);
        return ResponseEntity.ok(ApiResponse.ok("AI 분석 성공", response));
    }
}
