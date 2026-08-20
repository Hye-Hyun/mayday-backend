package com.mayday.domain.home.dto;

import java.time.LocalDate;

public class HomeSummaryResponse {

    private final long yearlyExpense;
    private final long aiFoundExpense;
    private final int recordedIncomeRatio;
    private final int recordedExpenseRatio;
    private final long recordedIncome;
    private final long recordedExpense;
    private final long aiClassifiedRecords;
    private final LocalDate taxDueDate;
    private final long taxDDay;

    public HomeSummaryResponse(
            long yearlyExpense,
            long aiFoundExpense,
            int recordedIncomeRatio,
            int recordedExpenseRatio,
            long recordedIncome,
            long recordedExpense,
            long aiClassifiedRecords,
            LocalDate taxDueDate,
            long taxDDay
    ) {
        this.yearlyExpense = yearlyExpense;
        this.aiFoundExpense = aiFoundExpense;
        this.recordedIncomeRatio = recordedIncomeRatio;
        this.recordedExpenseRatio = recordedExpenseRatio;
        this.recordedIncome = recordedIncome;
        this.recordedExpense = recordedExpense;
        this.aiClassifiedRecords = aiClassifiedRecords;
        this.taxDueDate = taxDueDate;
        this.taxDDay = taxDDay;
    }

    public long getYearlyExpense() {
        return yearlyExpense;
    }

    public long getAiFoundExpense() {
        return aiFoundExpense;
    }

    public int getRecordedIncomeRatio() {
        return recordedIncomeRatio;
    }

    public int getRecordedExpenseRatio() {
        return recordedExpenseRatio;
    }

    public long getRecordedIncome() {
        return recordedIncome;
    }

    public long getRecordedExpense() {
        return recordedExpense;
    }

    public long getAiClassifiedRecords() {
        return aiClassifiedRecords;
    }

    public LocalDate getTaxDueDate() {
        return taxDueDate;
    }

    public long getTaxDDay() {
        return taxDDay;
    }
}
