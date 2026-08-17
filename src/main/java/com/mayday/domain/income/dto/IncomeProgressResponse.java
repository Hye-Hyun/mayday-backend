package com.mayday.domain.income.dto;

import com.mayday.domain.income.model.IncomeStatus;

public class IncomeProgressResponse {

    private final Long cumulativeIncome;
    private final Long standardAmount;
    private final Long remainingAmount;
    private final IncomeStatus status;
    private final String message;
    private final boolean recommendExpenseRecord;

    public IncomeProgressResponse(
            Long cumulativeIncome,
            Long standardAmount,
            Long remainingAmount,
            IncomeStatus status,
            String message,
            boolean recommendExpenseRecord
    ) {
        this.cumulativeIncome = cumulativeIncome;
        this.standardAmount = standardAmount;
        this.remainingAmount = remainingAmount;
        this.status = status;
        this.message = message;
        this.recommendExpenseRecord = recommendExpenseRecord;
    }

    public Long getCumulativeIncome() { return cumulativeIncome; }
    public Long getStandardAmount() { return standardAmount; }
    public Long getRemainingAmount() { return remainingAmount; }
    public IncomeStatus getStatus() { return status; }
    public String getMessage() { return message; }
    public boolean isRecommendExpenseRecord() { return recommendExpenseRecord; }
}