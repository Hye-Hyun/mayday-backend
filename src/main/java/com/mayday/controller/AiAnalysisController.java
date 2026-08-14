package com.mayday.controller;

import com.mayday.domain.ai.ExpenseCategorySuggestionService;
import com.mayday.domain.ai.dto.ExpenseCategorySuggestionRequest;
import com.mayday.domain.ai.dto.ExpenseCategorySuggestionResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
public class AiAnalysisController {

    private final ExpenseCategorySuggestionService expenseCategorySuggestionService;

    public AiAnalysisController(ExpenseCategorySuggestionService expenseCategorySuggestionService) {
        this.expenseCategorySuggestionService = expenseCategorySuggestionService;
    }

    @PostMapping("/expense-category-suggestions")
    public ResponseEntity<ExpenseCategorySuggestionResponse> suggestExpenseCategory(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody ExpenseCategorySuggestionRequest request
    ) {
        return ResponseEntity.ok(expenseCategorySuggestionService.suggest(userId, request));
    }
}
