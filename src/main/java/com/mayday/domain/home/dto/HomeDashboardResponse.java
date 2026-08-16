package com.mayday.domain.home.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;

public class HomeDashboardResponse {

    private final TaxDeadlineSummary taxDeadline;
    private final ExpenseSummary expenseSummary;
    private final RatioSummary ratio;
    private final List<ShortcutResponse> shortcuts;

    public HomeDashboardResponse(
            TaxDeadlineSummary taxDeadline,
            ExpenseSummary expenseSummary,
            RatioSummary ratio,
            List<ShortcutResponse> shortcuts
    ) {
        this.taxDeadline = taxDeadline;
        this.expenseSummary = expenseSummary;
        this.ratio = ratio;
        this.shortcuts = shortcuts;
    }

    public TaxDeadlineSummary getTaxDeadline() {
        return taxDeadline;
    }

    public ExpenseSummary getExpenseSummary() {
        return expenseSummary;
    }

    public RatioSummary getRatio() {
        return ratio;
    }

    public List<ShortcutResponse> getShortcuts() {
        return shortcuts;
    }

    public static class TaxDeadlineSummary {
        private final LocalDate deadlineDate;
        private final long daysRemaining;
        private final String dDay;
        private final String message;

        public TaxDeadlineSummary(LocalDate deadlineDate, long daysRemaining, String message) {
            this.deadlineDate = deadlineDate;
            this.daysRemaining = daysRemaining;
            this.dDay = "D-" + daysRemaining;
            this.message = message;
        }

        public LocalDate getDeadlineDate() {
            return deadlineDate;
        }

        public long getDaysRemaining() {
            return daysRemaining;
        }

        @JsonProperty("dDay")
        public String getDDay() {
            return dDay;
        }

        public String getMessage() {
            return message;
        }
    }

    public static class ExpenseSummary {
        private final int year;
        private final int month;
        private final long yearlyExpenseAmount;
        private final long monthlyExpenseAmount;
        private final String message;

        public ExpenseSummary(
                int year,
                int month,
                long yearlyExpenseAmount,
                long monthlyExpenseAmount,
                String message
        ) {
            this.year = year;
            this.month = month;
            this.yearlyExpenseAmount = yearlyExpenseAmount;
            this.monthlyExpenseAmount = monthlyExpenseAmount;
            this.message = message;
        }

        public int getYear() {
            return year;
        }

        public int getMonth() {
            return month;
        }

        public long getYearlyExpenseAmount() {
            return yearlyExpenseAmount;
        }

        public long getMonthlyExpenseAmount() {
            return monthlyExpenseAmount;
        }

        public String getMessage() {
            return message;
        }
    }

    public static class RatioSummary {
        private final long expenseAmount;
        private final long incomeAmount;
        private final int expenseRate;
        private final int incomeRate;
        private final String message;

        public RatioSummary(
                long expenseAmount,
                long incomeAmount,
                int expenseRate,
                int incomeRate,
                String message
        ) {
            this.expenseAmount = expenseAmount;
            this.incomeAmount = incomeAmount;
            this.expenseRate = expenseRate;
            this.incomeRate = incomeRate;
            this.message = message;
        }

        public long getExpenseAmount() {
            return expenseAmount;
        }

        public long getIncomeAmount() {
            return incomeAmount;
        }

        public int getExpenseRate() {
            return expenseRate;
        }

        public int getIncomeRate() {
            return incomeRate;
        }

        public String getMessage() {
            return message;
        }
    }

    public static class ShortcutResponse {
        private final String key;
        private final String label;
        private final String target;
        private final boolean enabled;

        public ShortcutResponse(String key, String label, String target, boolean enabled) {
            this.key = key;
            this.label = label;
            this.target = target;
            this.enabled = enabled;
        }

        public String getKey() {
            return key;
        }

        public String getLabel() {
            return label;
        }

        public String getTarget() {
            return target;
        }

        public boolean isEnabled() {
            return enabled;
        }
    }
}
