package com.mayday.domain.mypage;

import com.mayday.domain.ai.model.ExpenseCategory;
import com.mayday.domain.expense.ExpenseRepository;
import com.mayday.domain.income.IncomeRepository;
import com.mayday.domain.mypage.dto.MyPageSummaryResponse;
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

class MyPageServiceTest {

    private static final long USER_ID = 1L;

    private final UserRepository userRepository = mock(UserRepository.class);
    private final ExpenseRepository expenseRepository = mock(ExpenseRepository.class);
    private final IncomeRepository incomeRepository = mock(IncomeRepository.class);
    private final Clock clock = Clock.fixed(
            Instant.parse("2026-08-17T00:00:00Z"),
            ZoneId.of("Asia/Seoul")
    );
    private final MyPageService myPageService = new MyPageService(
            userRepository,
            expenseRepository,
            incomeRepository,
            clock
    );

    @Test
    void getSummaryReturnsProfileTaxDeadlineAndYearlyRecordStats() {
        User user = new User("mayday@example.com", "encoded", true, true, true);
        ReflectionTestUtils.setField(user, "id", USER_ID);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(expenseRepository.countByUserIdAndDateBetweenAndDeletedFalse(
                USER_ID,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 12, 31)
        )).thenReturn(12L);
        when(incomeRepository.countByUserIdAndDateBetweenAndDeletedFalse(
                USER_ID,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 12, 31)
        )).thenReturn(5L);
        when(expenseRepository.countByUserIdAndDateBetweenAndDeletedFalseAndQualifiedEvidenceTrueAndCategoryIn(
                USER_ID,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 12, 31),
                expenseCategories()
        )).thenReturn(9L);
        when(expenseRepository.sumAmountByUserIdAndDateBetweenAndCategoryIn(
                eq(USER_ID),
                eq(LocalDate.of(2026, 1, 1)),
                eq(LocalDate.of(2026, 12, 31)),
                eq(expenseCategories())
        )).thenReturn(450_000L);

        MyPageSummaryResponse response = myPageService.getSummary(USER_ID, 2026);

        assertThat(response.getEmail()).isEqualTo("mayday@example.com");
        assertThat(response.getRecordedCount()).isEqualTo(17L);
        assertThat(response.getQualifiedEvidenceCount()).isEqualTo(9L);
        assertThat(response.getRecognizedExpense()).isEqualTo(450_000L);
        assertThat(response.getTaxDueDate()).isEqualTo(LocalDate.of(2027, 5, 31));
        assertThat(response.getTaxDDay()).isEqualTo(287L);
    }

    private List<ExpenseCategory> expenseCategories() {
        return Arrays.stream(ExpenseCategory.values())
                .filter(ExpenseCategory::isExpense)
                .toList();
    }
}
