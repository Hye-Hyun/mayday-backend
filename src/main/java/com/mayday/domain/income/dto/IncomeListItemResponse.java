package com.mayday.domain.income.dto;

import com.mayday.domain.ai.model.ExpenseCategory;
import com.mayday.domain.income.Income;

import java.time.LocalDate;

public class IncomeListItemResponse {

    private final String incomeId;
    private final LocalDate date;
    private final String merchantName;
    private final String itemName;
    private final Long amount;
    private final Long receivedAmount;
    private final boolean withholdingTaxApplied;
    private final Long withholdingTax;
    private final ExpenseCategory category;
    private final String remark;

    private IncomeListItemResponse(Income income, String incomeId) {
        this.incomeId = incomeId;
        this.date = income.getDate();
        this.merchantName = income.getMerchantName();
        this.itemName = income.getItemName();
        this.amount = income.getAmount();
        this.receivedAmount = income.getReceivedAmount();
        this.withholdingTaxApplied = income.isWithholdingTaxApplied();
        this.withholdingTax = income.getWithholdingTax();
        this.category = income.getCategory();
        this.remark = income.getRemark();
    }

    public static IncomeListItemResponse from(Income income, String incomeId) {
        return new IncomeListItemResponse(income, incomeId);
    }

    public String getIncomeId() { return incomeId; }
    public LocalDate getDate() { return date; }
    public String getMerchantName() { return merchantName; }
    public String getItemName() { return itemName; }
    public Long getAmount() { return amount; }
    public Long getReceivedAmount() { return receivedAmount; }
    public boolean isWithholdingTaxApplied() { return withholdingTaxApplied; }
    public Long getWithholdingTax() { return withholdingTax; }
    public ExpenseCategory getCategory() { return category; }
    public String getRemark() { return remark; }
}
