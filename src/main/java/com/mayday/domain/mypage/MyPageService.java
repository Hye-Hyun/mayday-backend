package com.mayday.domain.mypage;

import com.mayday.domain.ai.model.ExpenseCategory;
import com.mayday.domain.expense.ExpenseRepository;
import com.mayday.domain.income.IncomeRepository;
import com.mayday.domain.mypage.dto.MyPageSummaryResponse;
import com.mayday.domain.user.User;
import com.mayday.domain.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.Year;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

@Service
public class MyPageService {

    private static final int TAX_DEADLINE_MONTH = 5;
    private static final int TAX_DEADLINE_DAY = 31;

    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;
    private final IncomeRepository incomeRepository;
    private final Clock clock;

    public MyPageService(
            UserRepository userRepository,
            ExpenseRepository expenseRepository,
            IncomeRepository incomeRepository,
            Clock clock
    ) {
        this.userRepository = userRepository;
        this.expenseRepository = expenseRepository;
        this.incomeRepository = incomeRepository;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public MyPageSummaryResponse getSummary(Long userId, Integer year) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        LocalDate today = LocalDate.now(clock);
        int targetYear = year == null ? today.getYear() : year;
        validateYear(targetYear);
        LocalDate startDate = LocalDate.of(targetYear, 1, 1);
        LocalDate endDate = LocalDate.of(targetYear, 12, 31);

        long totalRecordCount = expenseRepository.countByUserIdAndDateBetweenAndDeletedFalse(
                userId,
                startDate,
                endDate
        ) + incomeRepository.countByUserIdAndDateBetweenAndDeletedFalse(
                userId,
                startDate,
                endDate
        );
        long qualifiedEvidenceCount = expenseRepository.countByUserIdAndDateBetweenAndDeletedFalseAndQualifiedEvidenceTrueAndCategoryIn(
                userId,
                startDate,
                endDate,
                expenseCategories()
        );
        long yearlyExpenseAmount = expenseRepository.sumAmountByUserIdAndDateBetweenAndCategoryIn(
                userId,
                startDate,
                endDate,
                expenseCategories()
        );

        return new MyPageSummaryResponse(
                user.getEmail(),
                totalRecordCount,
                qualifiedEvidenceCount,
                yearlyExpenseAmount,
                taxDueDate(today),
                ChronoUnit.DAYS.between(today, taxDueDate(today))
        );
    }

    private void validateYear(int year) {
        if (year < 2000 || year > Year.now(clock).getValue() + 1) {
            throw new IllegalArgumentException("조회 연도가 올바르지 않습니다");
        }
    }

    private LocalDate taxDueDate(LocalDate today) {
        LocalDate deadline = LocalDate.of(today.getYear(), TAX_DEADLINE_MONTH, TAX_DEADLINE_DAY);
        if (today.isAfter(deadline)) {
            return deadline.plusYears(1);
        }
        return deadline;
    }

    private List<ExpenseCategory> expenseCategories() {
        return Arrays.stream(ExpenseCategory.values())
                .filter(ExpenseCategory::isExpense)
                .toList();
    }
}
