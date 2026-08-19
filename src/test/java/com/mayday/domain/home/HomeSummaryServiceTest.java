package com.mayday.domain.home;

import com.mayday.domain.ai.model.ExpenseCategory;
import com.mayday.domain.expense.ExpenseRepository;
import com.mayday.domain.home.dto.HomeSummaryResponse;
import com.mayday.domain.income.IncomeRepository;
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

class HomeSummaryServiceTest {

    private static final long USER_ID = 1L;

    private final ExpenseRepository expenseRepository = mock(ExpenseRepository.class);
    private final IncomeRepository incomeRepository = mock(IncomeRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final Clock clock = Clock.fixed(
            Instant.parse("2026-08-17T00:00:00Z"),
            ZoneId.of("Asia/Seoul")
    );
    private final HomeSummaryService homeSummaryService = new HomeSummaryService(
            expenseRepository,
            incomeRepository,
            userRepository,
            clock
    );

    @Test
    void getSummaryReturnsApiSpecFields() {
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
        when(incomeRepository.sumAmountByUserIdAndDateBetween(
                USER_ID,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 12, 31)
        )).thenReturn(200_000L);
        when(expenseRepository.sumAiAnalyzedAmountByUserIdAndDateBetweenAndCategoryIn(
                eq(USER_ID),
                eq(LocalDate.of(2026, 8, 1)),
                eq(LocalDate.of(2026, 8, 31)),
                eq(expenseCategories())
        )).thenReturn(80_000L);
        when(expenseRepository.countByUserIdAndDateBetweenAndDeletedFalseAndAnalysisIdIsNotNull(
                USER_ID,
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 8, 31)
        )).thenReturn(4L);
        when(incomeRepository.countByUserIdAndDateBetweenAndDeletedFalseAndAnalysisIdIsNotNull(
                USER_ID,
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 8, 31)
        )).thenReturn(3L);

        HomeSummaryResponse response = homeSummaryService.getSummary(USER_ID, 2026, 8);

        assertThat(response.getYearlyExpense()).isEqualTo(300_000L);
        assertThat(response.getAiFoundExpense()).isEqualTo(80_000L);
        assertThat(response.getRecordedIncome()).isEqualTo(1_200_000L);
        assertThat(response.getRecordedExpense()).isEqualTo(300_000L);
        assertThat(response.getRecordedIncomeRatio()).isEqualTo(80);
        assertThat(response.getRecordedExpenseRatio()).isEqualTo(20);
        assertThat(response.getAiClassifiedRecords()).isEqualTo(7L);
        assertThat(response.getTaxDueDate()).isEqualTo(LocalDate.of(2027, 5, 31));
        assertThat(response.getTaxDDay()).isEqualTo(287L);
    }

    private List<ExpenseCategory> expenseCategories() {
        return Arrays.stream(ExpenseCategory.values())
                .filter(ExpenseCategory::isExpense)
                .toList();
    }

}
