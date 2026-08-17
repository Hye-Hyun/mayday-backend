package com.mayday.domain.expense.dto;

public class ExpenseCreateResponse {

    private final String expenseId;

    public ExpenseCreateResponse(String expenseId) {
        this.expenseId = expenseId;
    }

    public String getExpenseId() {
        return expenseId;
    }
}
