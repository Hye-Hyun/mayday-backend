package com.mayday.domain.income.dto;

public class IncomeCreateResponse {

    private final String incomeId;

    public IncomeCreateResponse(String incomeId) {
        this.incomeId = incomeId;
    }

    public String getIncomeId() { return incomeId; }
}