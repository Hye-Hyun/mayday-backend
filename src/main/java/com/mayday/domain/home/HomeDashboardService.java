package com.mayday.domain.home;

import com.mayday.domain.ai.model.ExpenseCategory;
import com.mayday.domain.expense.ExpenseRepository;
import com.mayday.domain.home.dto.HomeDashboardResponse;
import com.mayday.domain.home.dto.HomeDashboardResponse.ExpenseSummary;
import com.mayday.domain.home.dto.HomeDashboardResponse.RatioSummary;
import com.mayday.domain.home.dto.HomeDashboardResponse.ShortcutResponse;
import com.mayday.domain.home.dto.HomeDashboardResponse.TaxDeadlineSummary;
import com.mayday.domain.user.User;
import com.mayday.domain.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

@Service
public class HomeDashboardService {

    private static final int TAX_DEADLINE_MONTH = 5;
    private static final int TAX_DEADLINE_DAY = 31;

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final Clock clock;

    public HomeDashboardService(
            ExpenseRepository expenseRepository,
            UserRepository userRepository,
            Clock clock
    ) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public HomeDashboardResponse getDashboard(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        LocalDate today = LocalDate.now(clock);
        int year = today.getYear();

        long yearlyExpenseAmount = sumAmount(userId, startOfYear(year), endOfYear(year), expenseCategories());
        long monthlyExpenseAmount = sumAmount(userId, startOfMonth(today), endOfMonth(today), expenseCategories());
        long yearlyRecordedIncomeAmount = sumAmount(userId, startOfYear(year), endOfYear(year), incomeCategories());
        long yearlyIncomeAmount = nullToZero(user.getInitialIncome()) + yearlyRecordedIncomeAmount;
        long previousYearExpenseAmount = sumAmount(userId, startOfYear(year - 1), endOfYear(year - 1), expenseCategories());

        return new HomeDashboardResponse(
                createTaxDeadlineSummary(today),
                new ExpenseSummary(
                        year,
                        today.getMonthValue(),
                        yearlyExpenseAmount,
                        monthlyExpenseAmount,
                        "올해와 이번 달에 기록한 경비를 확인해보세요."
                ),
                createRatioSummary(yearlyExpenseAmount, yearlyIncomeAmount),
                createShortcuts(previousYearExpenseAmount > 0)
        );
    }

    private TaxDeadlineSummary createTaxDeadlineSummary(LocalDate today) {
        LocalDate deadline = LocalDate.of(today.getYear(), TAX_DEADLINE_MONTH, TAX_DEADLINE_DAY);
        if (today.isAfter(deadline)) {
            deadline = deadline.plusYears(1);
        }

        long daysRemaining = ChronoUnit.DAYS.between(today, deadline);
        return new TaxDeadlineSummary(
                deadline,
                daysRemaining,
                "종합소득세 신고 마감일까지 남은 기간을 확인해보세요."
        );
    }

    private RatioSummary createRatioSummary(long expenseAmount, long incomeAmount) {
        long totalAmount = expenseAmount + incomeAmount;
        int expenseRate = percentage(expenseAmount, totalAmount);
        int incomeRate = totalAmount == 0 ? 0 : 100 - expenseRate;

        return new RatioSummary(
                expenseAmount,
                incomeAmount,
                expenseRate,
                incomeRate,
                "올해 기록한 지출과 수입의 비율을 확인해보세요."
        );
    }

    private List<ShortcutResponse> createShortcuts(boolean exportEnabled) {
        return List.of(
                new ShortcutResponse("RECENT_RECORDS", "최근 기록 내역", "LEDGER", true),
                new ShortcutResponse("CREATE_EXPENSE", "경비 기록", "EXPENSE_RECORD", true),
                new ShortcutResponse("EXPORT", "내보내기", "LEDGER_EXPORT", exportEnabled)
        );
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

    private LocalDate startOfYear(int year) {
        return LocalDate.of(year, 1, 1);
    }

    private LocalDate endOfYear(int year) {
        return LocalDate.of(year, 12, 31);
    }

    private LocalDate startOfMonth(LocalDate date) {
        return date.withDayOfMonth(1);
    }

    private LocalDate endOfMonth(LocalDate date) {
        return date.withDayOfMonth(date.lengthOfMonth());
    }

    private List<ExpenseCategory> expenseCategories() {
        return Arrays.stream(ExpenseCategory.values())
                .filter(ExpenseCategory::isExpense)
                .toList();
    }

    private List<ExpenseCategory> incomeCategories() {
        return Arrays.stream(ExpenseCategory.values())
                .filter(ExpenseCategory::isIncome)
                .toList();
    }

    private long nullToZero(Long value) {
        return value == null ? 0L : value;
    }
}
