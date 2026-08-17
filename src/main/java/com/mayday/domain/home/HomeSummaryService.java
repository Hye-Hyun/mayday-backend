package com.mayday.domain.home;

import com.mayday.domain.ai.model.ExpenseCategory;
import com.mayday.domain.expense.ExpenseRepository;
import com.mayday.domain.home.dto.HomeSummaryResponse;
import com.mayday.domain.income.IncomeRepository;
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
public class HomeSummaryService {

    private static final int TAX_DEADLINE_MONTH = 5;
    private static final int TAX_DEADLINE_DAY = 31;

    private final ExpenseRepository expenseRepository;
    private final IncomeRepository incomeRepository;
    private final UserRepository userRepository;
    private final Clock clock;

    public HomeSummaryService(
            ExpenseRepository expenseRepository,
            IncomeRepository incomeRepository,
            UserRepository userRepository,
            Clock clock
    ) {
        this.expenseRepository = expenseRepository;
        this.incomeRepository = incomeRepository;
        this.userRepository = userRepository;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public HomeSummaryResponse getSummary(Long userId, Integer year, Integer month) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        LocalDate today = LocalDate.now(clock);
        int targetYear = year == null ? today.getYear() : year;
        int targetMonth = month == null ? today.getMonthValue() : month;
        validateYearAndMonth(targetYear, targetMonth);

        LocalDate yearStart = LocalDate.of(targetYear, 1, 1);
        LocalDate yearEnd = LocalDate.of(targetYear, 12, 31);
        LocalDate monthStart = LocalDate.of(targetYear, targetMonth, 1);
        LocalDate monthEnd = monthStart.withDayOfMonth(monthStart.lengthOfMonth());

        long recordedExpense = sumAmount(userId, yearStart, yearEnd, expenseCategories());
        long recordedIncome = nullToZero(user.getInitialIncome())
                + incomeRepository.sumAmountByUserIdAndDateBetween(userId, yearStart, yearEnd);
        long aiFoundExpense = expenseRepository.sumAiAnalyzedAmountByUserIdAndDateBetweenAndCategoryIn(
                userId,
                monthStart,
                monthEnd,
                expenseCategories()
        );
        long aiClassifiedRecords = expenseRepository.countByUserIdAndDateBetweenAndDeletedFalseAndAnalysisIdIsNotNull(
                userId,
                monthStart,
                monthEnd
        );

        long totalAmount = recordedIncome + recordedExpense;
        int recordedExpenseRatio = percentage(recordedExpense, totalAmount);
        int recordedIncomeRatio = totalAmount == 0 ? 0 : 100 - recordedExpenseRatio;
        LocalDate taxDueDate = taxDueDate(today);

        return new HomeSummaryResponse(
                recordedExpense,
                aiFoundExpense,
                recordedIncomeRatio,
                recordedExpenseRatio,
                recordedIncome,
                recordedExpense,
                aiClassifiedRecords,
                taxDueDate,
                ChronoUnit.DAYS.between(today, taxDueDate)
        );
    }

    private void validateYearAndMonth(int year, int month) {
        if (year < 2000 || year > Year.now(clock).getValue() + 1 || month < 1 || month > 12) {
            throw new IllegalArgumentException("조회 조건이 올바르지 않습니다");
        }
    }

    private long sumAmount(
            Long userId,
            LocalDate startDate,
            LocalDate endDate,
            List<ExpenseCategory> categories
    ) {
        return expenseRepository.sumAmountByUserIdAndDateBetweenAndCategoryIn(
                userId,
                startDate,
                endDate,
                categories
        );
    }

    private int percentage(long amount, long totalAmount) {
        if (totalAmount <= 0) {
            return 0;
        }
        return (int) Math.round((amount * 100.0) / totalAmount);
    }

    private LocalDate taxDueDate(LocalDate today) {
        LocalDate dueDate = LocalDate.of(today.getYear(), TAX_DEADLINE_MONTH, TAX_DEADLINE_DAY);
        if (today.isAfter(dueDate)) {
            return dueDate.plusYears(1);
        }
        return dueDate;
    }

    private List<ExpenseCategory> expenseCategories() {
        return Arrays.stream(ExpenseCategory.values())
                .filter(ExpenseCategory::isExpense)
                .toList();
    }

    private long nullToZero(Long value) {
        return value == null ? 0L : value;
    }
}
