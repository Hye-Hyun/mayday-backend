package com.mayday.domain.expense;

import com.mayday.domain.ai.EvidenceJudgmentPolicy;
import com.mayday.domain.ai.EvidenceJudgmentResult;
import com.mayday.domain.expense.dto.ExpenseAiRawResult;
import com.mayday.domain.expense.dto.ExpenseAnalyzeRequest;
import com.mayday.domain.expense.dto.ExpenseAnalyzeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExpenseAnalyzeService {

    private static final double WITHHOLDING_TAX_RATE = 0.967;

    private final ExpenseAiClient expenseAiClient;

    public ExpenseAnalyzeResponse analyze(ExpenseAnalyzeRequest request) {
        ExpenseAiRawResult raw = expenseAiClient.analyze(request.getRawText());

        applyWithholdingTaxPolicy(raw, request.isWithholdingTaxApplied());
        EvidenceJudgmentResult evidenceJudgment = EvidenceJudgmentPolicy.evaluate(
                raw.getType(),
                raw.getAmount(),
                raw.getEvidenceType(),
                raw.getCategory()
        );

        return ExpenseAnalyzeResponse.builder()
                .analysisId(generateAnalysisId())
                .type(raw.getType())
                .date(raw.getDate())
                .merchantName(raw.getMerchantName())
                .itemName(raw.getItemName())
                .amount(raw.getAmount())
                .category(raw.getCategory())
                .evidenceType(raw.getEvidenceType())
                .qualifiedEvidence(evidenceJudgment.isQualifiedEvidence())
                .evidenceJudgment(evidenceJudgment.getEvidenceJudgment().name())
                .expenseTreatmentPossible(evidenceJudgment.isExpenseTreatmentPossible())
                .evidenceReason(evidenceJudgment.getEvidenceReason())
                .reason(raw.getReason())
                .confidenceScore(raw.getConfidenceScore())
                .build();
    }

    private void applyWithholdingTaxPolicy(ExpenseAiRawResult raw, boolean withholdingTaxApplied) {
        if (withholdingTaxApplied && "INCOME".equals(raw.getType())) {
            int recalculatedAmount = (int) Math.round(raw.getAmount() / WITHHOLDING_TAX_RATE);
            raw.overrideAmount(recalculatedAmount);
        }
    }

    private String generateAnalysisId() {
        return "ana_" + UUID.randomUUID().toString().substring(0, 8);
    }
}
