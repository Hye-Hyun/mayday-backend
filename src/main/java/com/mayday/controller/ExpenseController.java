package com.mayday.controller;

import com.mayday.domain.ai.model.ExpenseCategory;
import com.mayday.domain.expense.ExpenseService;
import com.mayday.domain.expense.dto.CategoryOptionResponse;
import com.mayday.domain.expense.dto.CategoryOptionsResponse;
import com.mayday.domain.expense.dto.ExpenseCreateRequest;
import com.mayday.domain.expense.dto.ExpenseCreateResponse;
import com.mayday.domain.expense.dto.ExpenseDetailResponse;
import com.mayday.domain.expense.dto.ExpenseListResponse;
import com.mayday.domain.expense.dto.ExpenseUpdateRequest;
import com.mayday.domain.expense.dto.ExpenseUpdateResponse;
import com.mayday.global.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ExpenseCreateResponse>> createExpense(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody ExpenseCreateRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created("지출 기록 저장 성공", expenseService.create(userId, request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<ExpenseListResponse>> getExpenses(
            @AuthenticationPrincipal Long userId,
            @RequestParam(required = false) Integer year
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok("연도별 기록 목록 조회 성공", expenseService.getListByYear(userId, year))
        );
    }

    @GetMapping("/{expenseId}")
    public ResponseEntity<ApiResponse<ExpenseDetailResponse>> getExpense(
            @AuthenticationPrincipal Long userId,
            @PathVariable String expenseId
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok("지출 상세 조회 성공", expenseService.getDetail(userId, expenseId))
        );
    }

    @PatchMapping("/{expenseId}")
    public ResponseEntity<ApiResponse<ExpenseUpdateResponse>> updateExpense(
            @AuthenticationPrincipal Long userId,
            @PathVariable String expenseId,
            @Valid @RequestBody ExpenseUpdateRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok("지출 기록 수정 성공", expenseService.update(userId, expenseId, request))
        );
    }

    @DeleteMapping("/{expenseId}")
    public ResponseEntity<ApiResponse<Void>> deleteExpense(
            @AuthenticationPrincipal Long userId,
            @PathVariable String expenseId
    ) {
        expenseService.delete(userId, expenseId);
        return ResponseEntity.ok(ApiResponse.ok("지출 기록 삭제 성공", null));
    }

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<CategoryOptionsResponse>> getCategoryOptions() {
        List<CategoryOptionResponse> expenseCategories = Arrays.stream(ExpenseCategory.values())
                .filter(ExpenseCategory::isExpense)
                .map(category -> new CategoryOptionResponse(category.getLabel(), category.name()))
                .toList();

        List<CategoryOptionResponse> incomeCategories = Arrays.stream(ExpenseCategory.values())
                .filter(ExpenseCategory::isIncome)
                .map(category -> new CategoryOptionResponse(category.getLabel(), category.name()))
                .toList();

        CategoryOptionsResponse response = new CategoryOptionsResponse(expenseCategories, incomeCategories);

        return ResponseEntity.ok(ApiResponse.ok("계정과목 옵션 조회 성공", response));
    }
}
