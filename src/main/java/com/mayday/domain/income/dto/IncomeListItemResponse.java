package com.mayday.domain.income.dto;

import com.mayday.domain.income.Income;

import java.time.LocalDate;

public class IncomeListItemResponse {

    private final String incomeId;
    private final Long amount;
    private final LocalDate date;

    private IncomeListItemResponse(String incomeId, Long amount, LocalDate date) {
        this.incomeId = incomeId;
        this.amount = amount;
        this.date = date;
    }

    public static IncomeListItemResponse from(Income income, String incomeId) {
        return new IncomeListItemResponse(incomeId, income.getAmount(), income.getDate());
    }

    public String getIncomeId() { return incomeId; }
    public Long getAmount() { return amount; }
    public LocalDate getDate() { return date; }
}