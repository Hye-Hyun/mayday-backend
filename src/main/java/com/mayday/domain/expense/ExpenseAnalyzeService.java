package com.mayday.domain.expense;

import com.mayday.domain.expense.dto.ExpenseAiRawResult;
import com.mayday.domain.expense.dto.ExpenseAnalyzeRequest;
import com.mayday.domain.expense.dto.ExpenseAnalyzeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExpenseAnalyzeService {

    private static final int QUALIFIED_EVIDENCE_THRESHOLD = 30000;
    private static final double WITHHOLDING_TAX_RATE = 0.967;

    private final ExpenseAiClient expenseAiClient;

    public ExpenseAnalyzeResponse analyze(ExpenseAnalyzeRequest request) {
        ExpenseAiRawResult raw = expenseAiClient.analyze(request.getRawText());

        applyWithholdingTaxPolicy(raw, request.isWithholdingTaxApplied());
        applyQualifiedEvidencePolicy(raw);

        return ExpenseAnalyzeResponse.builder()
                .analysisId(generateAnalysisId())
                .type(raw.getType())
                .date(raw.getDate())
                .merchantName(raw.getMerchantName())
                .itemName(raw.getItemName())
                .amount(raw.getAmount())
                .category(raw.getCategory())
                .evidenceType(raw.getEvidenceType())
                .qualifiedEvidence(raw.isQualifiedEvidence())
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

    private void applyQualifiedEvidencePolicy(ExpenseAiRawResult raw) {
        boolean underThreshold = raw.getAmount() <= QUALIFIED_EVIDENCE_THRESHOLD;
        if (underThreshold && !raw.isQualifiedEvidence()) {
            String reason = raw.getReason() + " (3만 원 이하로 적격 후보로 분류되었으며, 최종 확정은 세무사 검토가 필요합니다.)";
            raw.overrideQualifiedEvidence(true, reason);
        }
    }

    private String generateAnalysisId() {
        return "ana_" + UUID.randomUUID().toString().substring(0, 8);
    }
}
