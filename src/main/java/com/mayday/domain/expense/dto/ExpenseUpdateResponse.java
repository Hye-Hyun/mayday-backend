package com.mayday.domain.expense.dto;

import java.time.OffsetDateTime;

public class ExpenseUpdateResponse {

    private final String expenseId;
    private final OffsetDateTime updatedAt;

    public ExpenseUpdateResponse(String expenseId, OffsetDateTime updatedAt) {
        this.expenseId = expenseId;
        this.updatedAt = updatedAt;
    }

    public String getExpenseId() {
        return expenseId;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }
}
