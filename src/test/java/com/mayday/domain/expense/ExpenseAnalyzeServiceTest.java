package com.mayday.domain.expense;

import com.mayday.domain.ai.model.EvidenceType;
import com.mayday.domain.ai.model.ExpenseCategory;
import com.mayday.domain.expense.dto.ExpenseAiRawResult;
import com.mayday.domain.expense.dto.ExpenseAnalyzeRequest;
import com.mayday.domain.expense.dto.ExpenseAnalyzeResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class ExpenseAnalyzeServiceTest {

    @Test
    void analyzeReplacesPlaceholderItemNameWithDash() {
        ExpenseAiClient expenseAiClient = rawText -> rawResult("string");
        ExpenseAnalyzeService service = new ExpenseAnalyzeService(expenseAiClient);

        ExpenseAnalyzeResponse response = service.analyze(request());

        assertThat(response.getItemName()).isEqualTo("-");
    }

    @Test
    void analyzeReplacesBlankItemNameWithDash() {
        ExpenseAiClient expenseAiClient = rawText -> rawResult(" ");
        ExpenseAnalyzeService service = new ExpenseAnalyzeService(expenseAiClient);

        ExpenseAnalyzeResponse response = service.analyze(request());

        assertThat(response.getItemName()).isEqualTo("-");
    }

    @Test
    void analyzeKeepsExtractedItemName() {
        ExpenseAiClient expenseAiClient = rawText -> rawResult("맥북 프로 14");
        ExpenseAnalyzeService service = new ExpenseAnalyzeService(expenseAiClient);

        ExpenseAnalyzeResponse response = service.analyze(request());

        assertThat(response.getItemName()).isEqualTo("맥북 프로 14");
    }

    @Test
    void analyzeReturnsNullEvidenceFieldsForIncome() {
        ExpenseAiClient expenseAiClient = rawText -> incomeRawResult();
        ExpenseAnalyzeService service = new ExpenseAnalyzeService(expenseAiClient);

        ExpenseAnalyzeResponse response = service.analyze(request());

        assertThat(response.getType()).isEqualTo("INCOME");
        assertThat(response.getEvidenceType()).isNull();
        assertThat(response.getQualifiedEvidence()).isNull();
    }

    private ExpenseAnalyzeRequest request() {
        ExpenseAnalyzeRequest request = BeanUtils.instantiateClass(ExpenseAnalyzeRequest.class);
        ReflectionTestUtils.setField(request, "rawText", "신용카드 매출전표");
        ReflectionTestUtils.setField(request, "withholdingTaxApplied", false);
        return request;
    }

    private ExpenseAiRawResult rawResult(String itemName) {
        ExpenseAiRawResult raw = BeanUtils.instantiateClass(ExpenseAiRawResult.class);
        ReflectionTestUtils.setField(raw, "type", "EXPENSE");
        ReflectionTestUtils.setField(raw, "date", "2026-08-03");
        ReflectionTestUtils.setField(raw, "merchantName", "애플코리아");
        ReflectionTestUtils.setField(raw, "itemName", itemName);
        ReflectionTestUtils.setField(raw, "amount", 2_490_000);
        ReflectionTestUtils.setField(raw, "category", ExpenseCategory.SUPPLIES.name());
        ReflectionTestUtils.setField(raw, "evidenceType", EvidenceType.CARD_RECEIPT.name());
        ReflectionTestUtils.setField(raw, "qualifiedEvidence", true);
        ReflectionTestUtils.setField(raw, "reason", "신용카드 매출전표로 확인되었습니다.");
        ReflectionTestUtils.setField(raw, "confidenceScore", 80);
        return raw;
    }

    private ExpenseAiRawResult incomeRawResult() {
        ExpenseAiRawResult raw = BeanUtils.instantiateClass(ExpenseAiRawResult.class);
        ReflectionTestUtils.setField(raw, "type", "INCOME");
        ReflectionTestUtils.setField(raw, "date", "2026-08-02");
        ReflectionTestUtils.setField(raw, "merchantName", "크몽");
        ReflectionTestUtils.setField(raw, "itemName", "디자인 용역");
        ReflectionTestUtils.setField(raw, "amount", 1_000_000);
        ReflectionTestUtils.setField(raw, "category", ExpenseCategory.SALES.name());
        ReflectionTestUtils.setField(raw, "evidenceType", EvidenceType.NON_QUALIFIED.name());
        ReflectionTestUtils.setField(raw, "qualifiedEvidence", false);
        ReflectionTestUtils.setField(raw, "reason", "수입 기록으로 확인되었습니다.");
        ReflectionTestUtils.setField(raw, "confidenceScore", 80);
        return raw;
    }
}
