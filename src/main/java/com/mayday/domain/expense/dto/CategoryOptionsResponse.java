package com.mayday.domain.expense.dto;

import java.util.List;

public class CategoryOptionsResponse {

    private final List<CategoryOptionResponse> expenseCategories;
    private final List<CategoryOptionResponse> incomeCategories;

    public CategoryOptionsResponse(
            List<CategoryOptionResponse> expenseCategories,
            List<CategoryOptionResponse> incomeCategories
    ) {
        this.expenseCategories = expenseCategories;
        this.incomeCategories = incomeCategories;
    }

    public List<CategoryOptionResponse> getExpenseCategories() {
        return expenseCategories;
    }

    public List<CategoryOptionResponse> getIncomeCategories() {
        return incomeCategories;
    }
}
