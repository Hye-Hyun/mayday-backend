package com.mayday.domain.ai;

import com.mayday.domain.ai.model.EvidenceJudgment;

public class EvidenceJudgmentResult {

    private final EvidenceJudgment evidenceJudgment;
    private final boolean qualifiedEvidence;
    private final boolean expenseTreatmentPossible;
    private final String evidenceReason;

    public EvidenceJudgmentResult(
            EvidenceJudgment evidenceJudgment,
            boolean qualifiedEvidence,
            boolean expenseTreatmentPossible,
            String evidenceReason
    ) {
        this.evidenceJudgment = evidenceJudgment;
        this.qualifiedEvidence = qualifiedEvidence;
        this.expenseTreatmentPossible = expenseTreatmentPossible;
        this.evidenceReason = evidenceReason;
    }

    public EvidenceJudgment getEvidenceJudgment() {
        return evidenceJudgment;
    }

    public boolean isQualifiedEvidence() {
        return qualifiedEvidence;
    }

    public boolean isExpenseTreatmentPossible() {
        return expenseTreatmentPossible;
    }

    public String getEvidenceReason() {
        return evidenceReason;
    }
}
