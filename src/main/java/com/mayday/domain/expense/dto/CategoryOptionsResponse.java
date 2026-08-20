package com.mayday.domain.expense.dto;

import java.util.List;

public class CategoryOptionsResponse {

    private final List<CategoryOptionResponse> expenseCategories;
    private final List<CategoryOptionResponse> incomeCategories;
    private final List<CategoryOptionResponse> evidenceTypes;

    public CategoryOptionsResponse(
            List<CategoryOptionResponse> expenseCategories,
            List<CategoryOptionResponse> incomeCategories,
            List<CategoryOptionResponse> evidenceTypes
    ) {
        this.expenseCategories = expenseCategories;
        this.incomeCategories = incomeCategories;
        this.evidenceTypes = evidenceTypes;
    }

    public List<CategoryOptionResponse> getExpenseCategories() {
        return expenseCategories;
    }

    public List<CategoryOptionResponse> getIncomeCategories() {
        return incomeCategories;
    }

    public List<CategoryOptionResponse> getEvidenceTypes() {
        return evidenceTypes;
    }
}
