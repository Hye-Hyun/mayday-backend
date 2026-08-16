package com.mayday.domain.ai;

import com.mayday.domain.ai.model.EvidenceJudgment;
import com.mayday.domain.ai.model.EvidenceType;
import com.mayday.domain.ai.model.ExpenseCategory;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EvidenceJudgmentPolicyTest {

    @Test
    void cardReceiptIsQualifiedEvidence() {
        EvidenceJudgmentResult result = EvidenceJudgmentPolicy.evaluate(
                "EXPENSE",
                50_000L,
                EvidenceType.CARD_RECEIPT,
                ExpenseCategory.SUPPLIES
        );

        assertThat(result.getEvidenceJudgment()).isEqualTo(EvidenceJudgment.QUALIFIED);
        assertThat(result.isQualifiedEvidence()).isTrue();
        assertThat(result.isExpenseTreatmentPossible()).isTrue();
    }

    @Test
    void smallExpenseIsQualifiedCandidateWithoutEvidenceKeyword() {
        EvidenceJudgmentResult result = EvidenceJudgmentPolicy.evaluate(
                "EXPENSE",
                30_000L,
                EvidenceType.UNKNOWN,
                ExpenseCategory.SUPPLIES
        );

        assertThat(result.getEvidenceJudgment()).isEqualTo(EvidenceJudgment.QUALIFIED);
        assertThat(result.isQualifiedEvidence()).isTrue();
        assertThat(result.getEvidenceReason()).contains("3만 원 이하");
    }

    @Test
    void largeSimpleReceiptIsNonQualifiedCandidate() {
        EvidenceJudgmentResult result = EvidenceJudgmentPolicy.evaluate(
                "EXPENSE",
                50_000L,
                EvidenceType.SIMPLE_RECEIPT,
                ExpenseCategory.SUPPLIES
        );

        assertThat(result.getEvidenceJudgment()).isEqualTo(EvidenceJudgment.NON_QUALIFIED);
        assertThat(result.isQualifiedEvidence()).isFalse();
        assertThat(result.isExpenseTreatmentPossible()).isTrue();
    }

    @Test
    void incomeRequiresSeparateReview() {
        EvidenceJudgmentResult result = EvidenceJudgmentPolicy.evaluate(
                "INCOME",
                967_000,
                "UNKNOWN",
                "SALES"
        );

        assertThat(result.getEvidenceJudgment()).isEqualTo(EvidenceJudgment.REVIEW_REQUIRED);
        assertThat(result.isQualifiedEvidence()).isFalse();
        assertThat(result.isExpenseTreatmentPossible()).isFalse();
    }
}
