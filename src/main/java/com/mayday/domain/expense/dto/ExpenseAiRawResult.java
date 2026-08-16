package com.mayday.domain.expense.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ExpenseAiRawResult {

    private String type;
    private String date;
    private String merchantName;
    private String itemName;
    private int amount;
    private String category;
    private String evidenceType;
    private boolean qualifiedEvidence;
    private String reason;
    private int confidenceScore;

    public void overrideAmount(int amount) {
        this.amount = amount;
    }

    public void overrideQualifiedEvidence(boolean qualifiedEvidence, String reason) {
        this.qualifiedEvidence = qualifiedEvidence;
        this.reason = reason;
    }
}