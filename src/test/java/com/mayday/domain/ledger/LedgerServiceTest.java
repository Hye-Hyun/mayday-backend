package com.mayday.domain.ledger;

import com.mayday.domain.ai.model.EvidenceType;
import com.mayday.domain.ai.model.ExpenseCategory;
import com.mayday.domain.expense.Expense;
import com.mayday.domain.expense.ExpenseRepository;
import com.mayday.domain.expense.dto.ExpenseCreateRequest;
import com.mayday.domain.income.Income;
import com.mayday.domain.income.IncomeRepository;
import com.mayday.domain.income.dto.IncomeCreateRequest;
import com.mayday.domain.ledger.dto.LedgerExportPreviewResponse;
import com.mayday.domain.ledger.dto.LedgerListResponse;
import com.mayday.domain.ledger.dto.LedgerYearsResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LedgerServiceTest {

    private static final long USER_ID = 1L;

    private final ExpenseRepository expenseRepository = mock(ExpenseRepository.class);
    private final IncomeRepository incomeRepository = mock(IncomeRepository.class);
    private final Clock clock = Clock.fixed(
            Instant.parse("2026-08-17T00:00:00Z"),
            ZoneId.of("Asia/Seoul")
    );
    private final LedgerService ledgerService = new LedgerService(expenseRepository, incomeRepository, clock);

    @Test
    void getLedgerReturnsTransactionsAndTotalCount() {
        when(expenseRepository.findByUserIdAndDateBetweenAndDeletedFalseOrderByDateDescIdDesc(
                USER_ID,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 12, 31)
        )).thenReturn(List.of(expense(ExpenseCategory.SUPPLIES, 15_000L)));
        when(incomeRepository.findByUserIdAndDateBetweenAndDeletedFalse(
                USER_ID,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 12, 31)
        )).thenReturn(List.of(income(100_000L)));

        LedgerListResponse response = ledgerService.getLedger(USER_ID, 2026);

        assertThat(response.getTotalCount()).isEqualTo(2);
        assertThat(response.getTransactions()).hasSize(2);
        assertThat(response.getTransactions().get(0).getType()).isEqualTo("INCOME");
        assertThat(response.getTransactions().get(1).getType()).isEqualTo("EXPENSE");
        assertThat(response.getTransactions().get(0).getEvidenceType()).isNull();
        assertThat(response.getTransactions().get(0).getQualifiedEvidence()).isNull();
    }

    @Test
    void getRecordedYearsReturnsYearsFromRepository() {
        when(expenseRepository.findRecordedYearsByUserId(USER_ID)).thenReturn(List.of(2026, 2025));
        when(incomeRepository.findRecordedYearsByUserId(USER_ID)).thenReturn(List.of(2026, 2024));

        LedgerYearsResponse response = ledgerService.getRecordedYears(USER_ID);

        assertThat(response.getYears()).containsExactly(2026, 2025, 2024);
    }

    @Test
    void getExportPreviewReturnsSummaryAndItems() {
        when(expenseRepository.findByUserIdAndDateBetweenAndDeletedFalseOrderByDateDescIdDesc(
                USER_ID,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 12, 31)
        )).thenReturn(List.of(expense(ExpenseCategory.SUPPLIES, 15_000L)));
        when(incomeRepository.findByUserIdAndDateBetweenAndDeletedFalse(
                USER_ID,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 12, 31)
        )).thenReturn(List.of(income(100_000L)));

        LedgerExportPreviewResponse response = ledgerService.getExportPreview(USER_ID, 2026);

        assertThat(response.getSummary().getExportYear()).isEqualTo(2026);
        assertThat(response.getSummary().getTotalRecordsCount()).isEqualTo(2);
        assertThat(response.getSummary().getTotalIncome()).isEqualTo(100_000L);
        assertThat(response.getSummary().getTotalExpense()).isEqualTo(15_000L);
        assertThat(response.getItems()).hasSize(2);
        assertThat(response.getItems().get(0).getIncome()).isEqualTo(100_000L);
        assertThat(response.getItems().get(0).getExpense()).isZero();
        assertThat(response.getItems().get(1).getIncome()).isZero();
        assertThat(response.getItems().get(1).getExpense()).isEqualTo(15_000L);
    }

    private Expense expense(ExpenseCategory category, Long amount) {
        ExpenseCreateRequest request = BeanUtils.instantiateClass(ExpenseCreateRequest.class);
        ReflectionTestUtils.setField(request, "analysisId", "ana_001");
        ReflectionTestUtils.setField(request, "date", LocalDate.of(2026, 8, 10));
        ReflectionTestUtils.setField(request, "merchantName", "알파문구");
        ReflectionTestUtils.setField(request, "itemName", "사무용품 및 필기구 구매");
        ReflectionTestUtils.setField(request, "amount", amount);
        ReflectionTestUtils.setField(request, "category", category);
        ReflectionTestUtils.setField(request, "evidenceType", EvidenceType.CARD_RECEIPT);
        ReflectionTestUtils.setField(request, "qualifiedEvidence", true);
        ReflectionTestUtils.setField(request, "remark", "확인 완료");
        return Expense.create(USER_ID, request);
    }

    private Income income(Long amount) {
        IncomeCreateRequest request = BeanUtils.instantiateClass(IncomeCreateRequest.class);
        ReflectionTestUtils.setField(request, "analysisId", "ana_002");
        ReflectionTestUtils.setField(request, "date", LocalDate.of(2026, 8, 11));
        ReflectionTestUtils.setField(request, "merchantName", "크몽");
        ReflectionTestUtils.setField(request, "itemName", "디자인 용역");
        ReflectionTestUtils.setField(request, "amount", amount);
        ReflectionTestUtils.setField(request, "receivedAmount", Math.round(amount * 0.967));
        ReflectionTestUtils.setField(request, "withholdingTaxApplied", true);
        ReflectionTestUtils.setField(request, "category", ExpenseCategory.SALES);
        ReflectionTestUtils.setField(request, "remark", "확인 완료");
        return Income.create(USER_ID, request);
    }
}
