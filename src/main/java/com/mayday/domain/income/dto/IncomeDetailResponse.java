package com.mayday.domain.income.dto;

import com.mayday.domain.ai.model.ExpenseCategory;
import com.mayday.domain.income.Income;

import java.time.LocalDate;

public class IncomeDetailResponse {

    private final String incomeId;
    private final LocalDate date;
    private final String merchantName;
    private final ExpenseCategory category;
    private final boolean withholding;
    private final long grossAmount;
    private final long withholdingTax;
    private final long amount;

    private IncomeDetailResponse(Income income, String incomeId) {
        this.incomeId = incomeId;
        this.date = income.getDate();
        this.merchantName = income.getMerchantName();
        this.category = income.getCategory();
        this.withholding = income.isWithholdingTaxApplied();
        this.grossAmount = income.getAmount();
        this.withholdingTax = income.getWithholdingTax();
        this.amount = income.getReceivedAmount();
    }

    public static IncomeDetailResponse from(Income income, String incomeId) {
        return new IncomeDetailResponse(income, incomeId);
    }

    public String getIncomeId() { return incomeId; }
    public LocalDate getDate() { return date; }
    public String getMerchantName() { return merchantName; }
    public ExpenseCategory getCategory() { return category; }
    public boolean isWithholding() { return withholding; }
    public long getGrossAmount() { return grossAmount; }
    public long getWithholdingTax() { return withholdingTax; }
    public long getAmount() { return amount; }
}
