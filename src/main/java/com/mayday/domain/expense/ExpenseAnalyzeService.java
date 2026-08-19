package com.mayday.domain.expense;

import com.mayday.domain.ai.EvidenceJudgmentPolicy;
import com.mayday.domain.ai.EvidenceJudgmentResult;
import com.mayday.domain.ai.model.ConfidenceLevel;
import com.mayday.domain.ai.model.EvidenceType;
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
                .evidenceType(normalizeEvidenceType(raw.getEvidenceType()))
                .qualifiedEvidence(evidenceJudgment.isQualifiedEvidence())
                .reason(buildReason(raw.getReason(), evidenceJudgment.getEvidenceReason()))
                .confidenceScore(raw.getConfidenceScore())
                .confidenceLevel(ConfidenceLevel.fromScore(raw.getConfidenceScore()).name())
                .build();
    }

    private void applyWithholdingTaxPolicy(ExpenseAiRawResult raw, boolean withholdingTaxApplied) {
        if (withholdingTaxApplied && "INCOME".equals(raw.getType())) {
            int recalculatedAmount = (int) Math.round(raw.getAmount() / WITHHOLDING_TAX_RATE);
            raw.overrideAmount(recalculatedAmount);
        }
    }

    private String normalizeEvidenceType(String evidenceType) {
        try {
            return EvidenceType.from(evidenceType).name();
        } catch (IllegalArgumentException e) {
            return EvidenceType.NON_QUALIFIED.name();
        }
    }

    private String buildReason(String aiReason, String evidenceReason) {
        if (aiReason == null || aiReason.isBlank()) {
            return evidenceReason;
        }
        return aiReason;
    }

    private String generateAnalysisId() {
        return "ana_" + UUID.randomUUID().toString().substring(0, 8);
    }
}
