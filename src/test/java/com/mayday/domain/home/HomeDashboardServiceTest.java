package com.mayday.domain.home;

import com.mayday.domain.ai.model.ExpenseCategory;
import com.mayday.domain.expense.ExpenseRepository;
import com.mayday.domain.home.dto.HomeDashboardResponse;
import com.mayday.domain.user.JobCategory;
import com.mayday.domain.user.User;
import com.mayday.domain.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HomeDashboardServiceTest {

    private static final long USER_ID = 1L;

    private final ExpenseRepository expenseRepository = mock(ExpenseRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final Clock clock = Clock.fixed(
            Instant.parse("2026-08-17T00:00:00Z"),
            ZoneId.of("Asia/Seoul")
    );
    private final HomeDashboardService homeDashboardService = new HomeDashboardService(
            expenseRepository,
            userRepository,
            clock
    );

    @Test
    void getDashboardReturnsTaxDeadlineExpenseSummaryRatioAndShortcuts() {
        User user = new User("mayday@example.com", "encoded", true, true, true);
        user.completeOnboarding(JobCategory.SALES_ORIENTED, 1_000_000L);
        ReflectionTestUtils.setField(user, "id", USER_ID);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(expenseRepository.sumAmountByUserIdAndDateBetweenAndCategoryIn(
                eq(USER_ID),
                eq(LocalDate.of(2026, 1, 1)),
                eq(LocalDate.of(2026, 12, 31)),
                eq(expenseCategories())
        )).thenReturn(300_000L);
        when(expenseRepository.sumAmountByUserIdAndDateBetweenAndCategoryIn(
                eq(USER_ID),
                eq(LocalDate.of(2026, 8, 1)),
                eq(LocalDate.of(2026, 8, 31)),
                eq(expenseCategories())
        )).thenReturn(80_000L);
        when(expenseRepository.sumAmountByUserIdAndDateBetweenAndCategoryIn(
                eq(USER_ID),
                eq(LocalDate.of(2026, 1, 1)),
                eq(LocalDate.of(2026, 12, 31)),
                eq(incomeCategories())
        )).thenReturn(200_000L);
        when(expenseRepository.sumAmountByUserIdAndDateBetweenAndCategoryIn(
                eq(USER_ID),
                eq(LocalDate.of(2025, 1, 1)),
                eq(LocalDate.of(2025, 12, 31)),
                eq(expenseCategories())
        )).thenReturn(500_000L);

        HomeDashboardResponse response = homeDashboardService.getDashboard(USER_ID);

        assertThat(response.getTaxDeadline().getDeadlineDate()).isEqualTo(LocalDate.of(2027, 5, 31));
        assertThat(response.getTaxDeadline().getDDay()).isEqualTo("D-287");
        assertThat(response.getExpenseSummary().getYearlyExpenseAmount()).isEqualTo(300_000L);
        assertThat(response.getExpenseSummary().getMonthlyExpenseAmount()).isEqualTo(80_000L);
        assertThat(response.getRatio().getExpenseAmount()).isEqualTo(300_000L);
        assertThat(response.getRatio().getIncomeAmount()).isEqualTo(1_200_000L);
        assertThat(response.getRatio().getExpenseRate()).isEqualTo(20);
        assertThat(response.getRatio().getIncomeRate()).isEqualTo(80);
        assertThat(response.getShortcuts()).hasSize(3);
        assertThat(response.getShortcuts().get(2).isEnabled()).isTrue();
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
}
