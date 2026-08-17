package com.mayday.domain.expense.dto;

import com.mayday.domain.ai.model.EvidenceType;
import com.mayday.domain.ai.model.ExpenseCategory;
import com.mayday.domain.expense.Expense;

import java.time.LocalDate;

public class ExpenseDetailResponse {

    private final String expenseId;
    private final String analysisId;
    private final LocalDate date;
    private final String merchantName;
    private final String itemName;
    private final Long amount;
    private final ExpenseCategory category;
    private final EvidenceType evidenceType;
    private final Boolean qualifiedEvidence;
    private final String reason;
    private final String remark;

    private ExpenseDetailResponse(Expense expense, String expenseId) {
        this.expenseId = expenseId;
        this.analysisId = expense.getAnalysisId();
        this.date = expense.getDate();
        this.merchantName = expense.getMerchantName();
        this.itemName = expense.getItemName();
        this.amount = expense.getAmount();
        this.category = expense.getCategory();
        this.evidenceType = expense.getEvidenceType();
        this.qualifiedEvidence = expense.getQualifiedEvidence();
        this.reason = expense.getReason();
        this.remark = expense.getRemark();
    }

    public static ExpenseDetailResponse from(Expense expense, String expenseId) {
        return new ExpenseDetailResponse(expense, expenseId);
    }

    public String getExpenseId() { return expenseId; }

    public String getAnalysisId() { return analysisId; }

    public LocalDate getDate() { return date; }

    public String getMerchantName() { return merchantName; }

    public String getItemName() { return itemName; }

    public Long getAmount() { return amount; }

    public ExpenseCategory getCategory() { return category; }

    public EvidenceType getEvidenceType() { return evidenceType; }

    public Boolean getQualifiedEvidence() { return qualifiedEvidence; }

    public String getReason() { return reason; }

    public String getRemark() { return remark; }
}
