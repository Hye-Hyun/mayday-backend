package com.mayday.domain.income.dto;

import java.util.List;

public class IncomeListResponse {

    private final int year;
    private final int count;
    private final List<IncomeListItemResponse> incomes;

    public IncomeListResponse(int year, int count, List<IncomeListItemResponse> incomes) {
        this.year = year;
        this.count = count;
        this.incomes = incomes;
    }

    public int getYear() { return year; }
    public int getCount() { return count; }
    public List<IncomeListItemResponse> getIncomes() { return incomes; }
}