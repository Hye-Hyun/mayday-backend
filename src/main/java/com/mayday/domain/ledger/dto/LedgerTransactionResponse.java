package com.mayday.domain.ledger.dto;

import com.mayday.domain.ai.model.EvidenceType;
import com.mayday.domain.ai.model.ExpenseCategory;
import com.mayday.domain.expense.Expense;
import com.mayday.domain.income.Income;

import java.time.LocalDate;

public class LedgerTransactionResponse {

    private final String incomeId;
    private final String expenseId;
    private final String analysisId;
    private final String type;
    private final LocalDate date;
    private final String merchantName;
    private final String itemName;
    private final Long amount;
    private final ExpenseCategory category;
    private final EvidenceType evidenceType;
    private final Boolean qualifiedEvidence;

    private LedgerTransactionResponse(Expense expense) {
        this.incomeId = null;
        this.expenseId = formatExpenseId(expense.getId());
        this.analysisId = expense.getAnalysisId();
        this.type = expense.getCategory().isIncome() ? "INCOME" : "EXPENSE";
        this.date = expense.getDate();
        this.merchantName = expense.getMerchantName();
        this.itemName = expense.getItemName();
        this.amount = expense.getAmount();
        this.category = expense.getCategory();
        this.evidenceType = expense.getCategory().isIncome() ? null : expense.getEvidenceType();
        this.qualifiedEvidence = expense.getCategory().isIncome() ? null : expense.getQualifiedEvidence();
    }

    private LedgerTransactionResponse(Income income) {
        this.incomeId = formatIncomeId(income.getId());
        this.expenseId = null;
        this.analysisId = income.getAnalysisId();
        this.type = "INCOME";
        this.date = income.getDate();
        this.merchantName = income.getMerchantName();
        this.itemName = income.getItemName();
        this.amount = income.getAmount();
        this.category = income.getCategory();
        this.evidenceType = null;
        this.qualifiedEvidence = null;
    }

    public static LedgerTransactionResponse from(Expense expense) {
        return new LedgerTransactionResponse(expense);
    }

    public static LedgerTransactionResponse from(Income income) {
        return new LedgerTransactionResponse(income);
    }

    public String getIncomeId() {
        return incomeId;
    }

    public String getExpenseId() {
        return expenseId;
    }

    public String getAnalysisId() {
        return analysisId;
    }

    public String getType() {
        return type;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public String getItemName() {
        return itemName;
    }

    public Long getAmount() {
        return amount;
    }

    public ExpenseCategory getCategory() {
        return category;
    }

    public EvidenceType getEvidenceType() {
        return evidenceType;
    }

    public Boolean getQualifiedEvidence() {
        return qualifiedEvidence;
    }

    private String formatIncomeId(Long id) {
        return id == null ? null : "inc_" + String.format("%03d", id);
    }

    private String formatExpenseId(Long id) {
        return id == null ? null : "exp_" + String.format("%03d", id);
    }
}
