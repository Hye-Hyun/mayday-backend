package com.mayday.domain.expense.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ExpenseAnalyzeRequest {

    private String sourceType;
    private String sourceId;
    private String rawText;
    private boolean withholdingTaxApplied;
}
