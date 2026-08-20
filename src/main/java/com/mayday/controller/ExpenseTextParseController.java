package com.mayday.controller;

import com.mayday.domain.expense.ExpenseTextParseService;
import com.mayday.domain.expense.dto.ExpenseTextParseRequest;
import com.mayday.domain.expense.dto.ExpenseTextParseResponse;
import com.mayday.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/expenses")
public class ExpenseTextParseController {

    private final ExpenseTextParseService expenseTextParseService;

    @PostMapping("/text/parse")
    public ResponseEntity<ApiResponse<ExpenseTextParseResponse>> parseText(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody ExpenseTextParseRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok("텍스트 분석 입력 저장 성공", expenseTextParseService.parse(userId, request))
        );
    }
}
