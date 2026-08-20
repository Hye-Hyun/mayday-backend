package com.mayday.controller;

import com.mayday.domain.ai.ExpenseCategorySuggestionService;
import com.mayday.domain.ai.dto.ExpenseAnalysisRequest;
import com.mayday.domain.ai.dto.ExpenseAnalysisResponse;
import com.mayday.global.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/expenses")
public class AiAnalysisController {

    private final ExpenseCategorySuggestionService expenseCategorySuggestionService;

    public AiAnalysisController(ExpenseCategorySuggestionService expenseCategorySuggestionService) {
        this.expenseCategorySuggestionService = expenseCategorySuggestionService;
    }

    @PostMapping("/analyze/rule-based")
    public ResponseEntity<ApiResponse<ExpenseAnalysisResponse>> analyzeExpense(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody ExpenseAnalysisRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok("AI 분석 성공", expenseCategorySuggestionService.analyze(userId, request))
        );
    }
}
