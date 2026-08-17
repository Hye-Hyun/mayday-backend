package com.mayday.domain.ledger.dto;

import com.mayday.domain.ai.model.EvidenceType;
import com.mayday.domain.ai.model.ExpenseCategory;
import com.mayday.domain.expense.Expense;

import java.time.LocalDate;
import java.util.List;

public class LedgerExportPreviewResponse {

    private final Summary summary;
    private final List<Item> items;

    public LedgerExportPreviewResponse(Summary summary, List<Item> items) {
        this.summary = summary;
        this.items = items;
    }

    public Summary getSummary() {
        return summary;
    }

    public List<Item> getItems() {
        return items;
    }

    public static class Summary {
        private final int exportYear;
        private final int totalRecordsCount;
        private final long totalIncome;
        private final long totalExpense;

        public Summary(int exportYear, int totalRecordsCount, long totalIncome, long totalExpense) {
            this.exportYear = exportYear;
            this.totalRecordsCount = totalRecordsCount;
            this.totalIncome = totalIncome;
            this.totalExpense = totalExpense;
        }

        public int getExportYear() {
            return exportYear;
        }

        public int getTotalRecordsCount() {
            return totalRecordsCount;
        }

        public long getTotalIncome() {
            return totalIncome;
        }

        public long getTotalExpense() {
            return totalExpense;
        }
    }

    public static class Item {
        private final String analysisId;
        private final String type;
        private final LocalDate date;
        private final String merchantName;
        private final String itemName;
        private final Long amount;
        private final long income;
        private final long expense;
        private final ExpenseCategory category;
        private final EvidenceType evidenceType;
        private final Boolean qualifiedEvidence;
        private final String remark;

        private Item(Expense transaction) {
            boolean incomeRecord = transaction.getCategory().isIncome();
            this.analysisId = transaction.getAnalysisId();
            this.type = incomeRecord ? "INCOME" : "EXPENSE";
            this.date = transaction.getDate();
            this.merchantName = transaction.getMerchantName();
            this.itemName = transaction.getItemName();
            this.amount = transaction.getAmount();
            this.income = incomeRecord ? transaction.getAmount() : 0L;
            this.expense = incomeRecord ? 0L : transaction.getAmount();
            this.category = transaction.getCategory();
            this.evidenceType = incomeRecord ? null : transaction.getEvidenceType();
            this.qualifiedEvidence = incomeRecord ? null : transaction.getQualifiedEvidence();
            this.remark = transaction.getRemark();
        }

        public static Item from(Expense transaction) {
            return new Item(transaction);
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

        public long getIncome() {
            return income;
        }

        public long getExpense() {
            return expense;
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

        public String getRemark() {
            return remark;
        }
    }
}
