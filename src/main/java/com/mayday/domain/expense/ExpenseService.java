package com.mayday.domain.expense;

import com.mayday.domain.expense.dto.ExpenseCreateRequest;
import com.mayday.domain.expense.dto.ExpenseCreateResponse;
import com.mayday.domain.expense.dto.ExpenseDetailResponse;
import com.mayday.domain.expense.dto.ExpenseListItemResponse;
import com.mayday.domain.expense.dto.ExpenseListResponse;
import com.mayday.domain.expense.dto.ExpenseUpdateRequest;
import com.mayday.domain.expense.dto.ExpenseUpdateResponse;
import com.mayday.global.exception.ExpenseAccessDeniedException;
import com.mayday.global.exception.ExpenseNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.Year;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class ExpenseService {

    private static final String EXPENSE_ID_PREFIX = "exp_";
    private static final ZoneOffset KOREA_OFFSET = ZoneOffset.ofHours(9);

    private final ExpenseRepository expenseRepository;

    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    @Transactional
    public ExpenseCreateResponse create(Long userId, ExpenseCreateRequest request) {
        Expense expense = expenseRepository.save(Expense.create(userId, request));
        return new ExpenseCreateResponse(formatExpenseId(expense.getId()));
    }

    @Transactional(readOnly = true)
    public ExpenseDetailResponse getDetail(Long userId, String expenseId) {
        Expense expense = getExpense(expenseId);
        validateOwner(userId, expense);
        return ExpenseDetailResponse.from(expense, formatExpenseId(expense.getId()));
    }

    @Transactional(readOnly = true)
    public ExpenseListResponse getListByYear(Long userId, Integer year) {
        int targetYear = year == null ? Year.now().getValue() : year;
        LocalDate startDate = LocalDate.of(targetYear, 1, 1);
        LocalDate endDate = LocalDate.of(targetYear, 12, 31);

        List<ExpenseListItemResponse> expenses = expenseRepository
                .findByUserIdAndDateBetweenAndDeletedFalseOrderByDateDescIdDesc(
                        userId,
                        startDate,
                        endDate
                )
                .stream()
                .map(expense -> ExpenseListItemResponse.from(expense, formatExpenseId(expense.getId())))
                .toList();

        return new ExpenseListResponse(targetYear, expenses.size(), expenses);
    }

    @Transactional
    public ExpenseUpdateResponse update(Long userId, String expenseId, ExpenseUpdateRequest request) {
        Expense expense = getExpense(expenseId);
        validateOwner(userId, expense);
        expense.update(request);

        return new ExpenseUpdateResponse(
                formatExpenseId(expense.getId()),
                OffsetDateTime.of(expense.getUpdatedAt(), KOREA_OFFSET)
        );
    }

    @Transactional
    public void delete(Long userId, String expenseId) {
        Expense expense = getExpense(expenseId);
        validateOwner(userId, expense);
        expense.delete();
    }

    private Expense getExpense(String expenseId) {
        Long id = parseExpenseId(expenseId);
        return expenseRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ExpenseNotFoundException("지출 기록을 찾을 수 없습니다"));
    }

    private void validateOwner(Long userId, Expense expense) {
        if (!expense.getUserId().equals(userId)) {
            throw new ExpenseAccessDeniedException("수정 권한이 없습니다");
        }
    }

    private Long parseExpenseId(String expenseId) {
        try {
            String id = expenseId.startsWith(EXPENSE_ID_PREFIX)
                    ? expenseId.substring(EXPENSE_ID_PREFIX.length())
                    : expenseId;
            return Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new ExpenseNotFoundException("지출 기록을 찾을 수 없습니다");
        }
    }

    private String formatExpenseId(Long id) {
        return EXPENSE_ID_PREFIX + id;
    }
}
