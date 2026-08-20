package com.mayday.domain.expense.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExpenseAnalyzeResponse {

    private String analysisId;
    private String type;
    private String date;
    private String merchantName;
    private String itemName;
    private int amount;
    private String category;
    private String evidenceType;
    private Boolean qualifiedEvidence;
    private String reason;
    private int confidenceScore;
    private String confidenceLevel;
}
