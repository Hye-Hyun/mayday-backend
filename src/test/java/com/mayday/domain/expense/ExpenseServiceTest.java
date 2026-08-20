package com.mayday.domain.expense;

import com.mayday.domain.ai.model.EvidenceType;
import com.mayday.domain.ai.model.ExpenseCategory;
import com.mayday.domain.expense.dto.ExpenseCreateRequest;
import com.mayday.domain.expense.dto.ExpenseCreateResponse;
import com.mayday.domain.expense.dto.ExpenseDetailResponse;
import com.mayday.domain.expense.dto.ExpenseUpdateRequest;
import com.mayday.domain.expense.dto.ExpenseUpdateResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ExpenseServiceTest {

    private static final long USER_ID = 1L;

    private final ExpenseRepository expenseRepository = mock(ExpenseRepository.class);
    private final ExpenseService expenseService = new ExpenseService(expenseRepository);

    @Test
    void createSavesUserConfirmedExpenseRecord() {
        when(expenseRepository.save(any(Expense.class))).thenAnswer(invocation -> {
            Expense expense = invocation.getArgument(0);
            ReflectionTestUtils.setField(expense, "id", 1L);
            return expense;
        });

        ExpenseCreateResponse response = expenseService.create(USER_ID, createRequest(false));

        assertThat(response.getExpenseId()).isEqualTo("exp_001");
    }

    @Test
    void getDetailReturnsAnalysisReasonAndRemark() {
        Expense expense = savedExpense(createRequest(true));
        when(expenseRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(expense));

        ExpenseDetailResponse response = expenseService.getDetail(USER_ID, "exp_001");

        assertThat(response.getExpenseId()).isEqualTo("exp_001");
        assertThat(response.getAnalysisId()).isEqualTo("ana_001");
        assertThat(response.getReason()).isEqualTo("카드 매출전표로 확인되어 적격 증빙으로 분류했습니다.");
        assertThat(response.getRemark()).isEqualTo("확인 완료");
    }

    @Test
    void updateAppliesUserEditedFinalValues() {
        Expense expense = savedExpense(createRequest(true));
        when(expenseRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(expense));

        ExpenseUpdateResponse response = expenseService.update(USER_ID, "exp_001", updateRequest());

        assertThat(response.getExpenseId()).isEqualTo("exp_001");
        assertThat(response.getUpdatedAt()).isNotNull();
        assertThat(expense.getItemName()).isEqualTo("사무용품 구매");
        assertThat(expense.getQualifiedEvidence()).isFalse();
        assertThat(expense.getEvidenceType()).isEqualTo(EvidenceType.NON_QUALIFIED);
        assertThat(expense.getRemark()).isEqualTo("사용자 수정");
    }

    private Expense savedExpense(ExpenseCreateRequest request) {
        Expense expense = Expense.create(USER_ID, request);
        ReflectionTestUtils.setField(expense, "id", 1L);
        ReflectionTestUtils.setField(expense, "createdAt", LocalDate.of(2026, 8, 10).atStartOfDay());
        ReflectionTestUtils.setField(expense, "updatedAt", LocalDate.of(2026, 8, 10).atStartOfDay());
        return expense;
    }

    private ExpenseCreateRequest createRequest(boolean qualifiedEvidence) {
        ExpenseCreateRequest request = BeanUtils.instantiateClass(ExpenseCreateRequest.class);
        ReflectionTestUtils.setField(request, "analysisId", "ana_001");
        ReflectionTestUtils.setField(request, "date", LocalDate.of(2026, 8, 10));
        ReflectionTestUtils.setField(request, "merchantName", "알파문구");
        ReflectionTestUtils.setField(request, "itemName", "사무용품 및 필기구 구매");
        ReflectionTestUtils.setField(request, "amount", 15_000L);
        ReflectionTestUtils.setField(request, "category", ExpenseCategory.SUPPLIES);
        ReflectionTestUtils.setField(request, "evidenceType", EvidenceType.CARD_RECEIPT);
        ReflectionTestUtils.setField(request, "qualifiedEvidence", qualifiedEvidence);
        ReflectionTestUtils.setField(request, "reason", "카드 매출전표로 확인되어 적격 증빙으로 분류했습니다.");
        ReflectionTestUtils.setField(request, "remark", "확인 완료");
        return request;
    }

    private ExpenseUpdateRequest updateRequest() {
        ExpenseUpdateRequest request = BeanUtils.instantiateClass(ExpenseUpdateRequest.class);
        ReflectionTestUtils.setField(request, "date", LocalDate.of(2026, 8, 10));
        ReflectionTestUtils.setField(request, "merchantName", "알파문구");
        ReflectionTestUtils.setField(request, "itemName", "사무용품 구매");
        ReflectionTestUtils.setField(request, "amount", 15_000L);
        ReflectionTestUtils.setField(request, "category", ExpenseCategory.SUPPLIES);
        ReflectionTestUtils.setField(request, "evidenceType", EvidenceType.NON_QUALIFIED);
        ReflectionTestUtils.setField(request, "qualifiedEvidence", false);
        ReflectionTestUtils.setField(request, "remark", "사용자 수정");
        return request;
    }
}
