package com.mayday.domain.expense.dto;

import java.util.List;

public class ExpenseListResponse {

    private final int year;
    private final int totalCount;
    private final List<ExpenseListItemResponse> records;

    public ExpenseListResponse(int year, int totalCount, List<ExpenseListItemResponse> records) {
        this.year = year;
        this.totalCount = totalCount;
        this.records = records;
    }

    public int getYear() { return year; }

    public int getTotalCount() { return totalCount; }

    public List<ExpenseListItemResponse> getRecords() { return records; }
}
