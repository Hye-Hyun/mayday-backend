package com.mayday.domain.income;

import com.mayday.domain.ai.model.ExpenseCategory;
import com.mayday.domain.income.dto.IncomeCreateRequest;
import com.mayday.domain.income.dto.IncomeCreateResponse;
import com.mayday.domain.income.dto.IncomeDetailResponse;
import com.mayday.domain.income.dto.IncomeUpdateRequest;
import com.mayday.domain.income.dto.IncomeUpdateResponse;
import com.mayday.domain.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class IncomeServiceTest {

    private static final long USER_ID = 1L;

    private final IncomeRepository incomeRepository = mock(IncomeRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final IncomeService incomeService = new IncomeService(incomeRepository, userRepository);

    @Test
    void createSavesIncomeRecordWithWithholdingFields() {
        when(incomeRepository.save(any(Income.class))).thenAnswer(invocation -> {
            Income income = invocation.getArgument(0);
            ReflectionTestUtils.setField(income, "id", 1L);
            return income;
        });

        IncomeCreateResponse response = incomeService.create(USER_ID, createRequest());

        assertThat(response.getIncomeId()).isEqualTo("inc_001");
    }

    @Test
    void getDetailReturnsApiSpecFields() {
        Income income = savedIncome(createRequest());
        when(incomeRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(income));

        IncomeDetailResponse response = incomeService.getDetail(USER_ID, "inc_001");

        assertThat(response.getIncomeId()).isEqualTo("inc_001");
        assertThat(response.getDate()).isEqualTo(LocalDate.of(2026, 8, 2));
        assertThat(response.getMerchantName()).isEqualTo("크몽");
        assertThat(response.getCategory()).isEqualTo(ExpenseCategory.SALES);
        assertThat(response.isWithholding()).isTrue();
        assertThat(response.getGrossAmount()).isEqualTo(1_000_000L);
        assertThat(response.getWithholdingTax()).isEqualTo(33_000L);
        assertThat(response.getAmount()).isEqualTo(967_000L);
    }

    @Test
    void updateAppliesUserEditedIncomeValues() {
        Income income = savedIncome(createRequest());
        when(incomeRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(income));

        IncomeUpdateResponse response = incomeService.update(USER_ID, "inc_001", updateRequest());

        assertThat(response.getIncomeId()).isEqualTo("inc_001");
        assertThat(response.getUpdatedAt()).isNotNull();
        assertThat(income.getMerchantName()).isEqualTo("숨고");
        assertThat(income.getAmount()).isEqualTo(500_000L);
        assertThat(income.getReceivedAmount()).isEqualTo(500_000L);
        assertThat(income.isWithholdingTaxApplied()).isFalse();
        assertThat(income.getCategory()).isEqualTo(ExpenseCategory.OTHER_INCOME);
    }

    @Test
    void deleteMarksIncomeRecordDeleted() {
        Income income = savedIncome(createRequest());
        when(incomeRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(income));

        incomeService.delete(USER_ID, "inc_001");

        assertThat(income.isDeleted()).isTrue();
    }

    private Income savedIncome(IncomeCreateRequest request) {
        Income income = Income.create(USER_ID, request);
        ReflectionTestUtils.setField(income, "id", 1L);
        ReflectionTestUtils.setField(income, "createdAt", LocalDate.of(2026, 8, 2).atStartOfDay());
        ReflectionTestUtils.setField(income, "updatedAt", LocalDate.of(2026, 8, 2).atStartOfDay());
        return income;
    }

    private IncomeCreateRequest createRequest() {
        IncomeCreateRequest request = BeanUtils.instantiateClass(IncomeCreateRequest.class);
        ReflectionTestUtils.setField(request, "analysisId", "ana_071");
        ReflectionTestUtils.setField(request, "date", LocalDate.of(2026, 8, 2));
        ReflectionTestUtils.setField(request, "merchantName", "크몽");
        ReflectionTestUtils.setField(request, "itemName", "디자인 용역");
        ReflectionTestUtils.setField(request, "amount", 1_000_000L);
        ReflectionTestUtils.setField(request, "receivedAmount", 967_000L);
        ReflectionTestUtils.setField(request, "withholdingTaxApplied", true);
        ReflectionTestUtils.setField(request, "category", ExpenseCategory.SALES);
        ReflectionTestUtils.setField(request, "remark", null);
        return request;
    }

    private IncomeUpdateRequest updateRequest() {
        IncomeUpdateRequest request = BeanUtils.instantiateClass(IncomeUpdateRequest.class);
        ReflectionTestUtils.setField(request, "date", LocalDate.of(2026, 8, 3));
        ReflectionTestUtils.setField(request, "merchantName", "숨고");
        ReflectionTestUtils.setField(request, "itemName", "촬영 보조");
        ReflectionTestUtils.setField(request, "amount", 500_000L);
        ReflectionTestUtils.setField(request, "receivedAmount", 500_000L);
        ReflectionTestUtils.setField(request, "withholdingTaxApplied", false);
        ReflectionTestUtils.setField(request, "category", ExpenseCategory.OTHER_INCOME);
        ReflectionTestUtils.setField(request, "remark", "사용자 수정");
        return request;
    }
}
