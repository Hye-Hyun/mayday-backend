package com.mayday.domain.ledger.dto;

import java.util.List;

public class LedgerListResponse {

    private final List<LedgerTransactionResponse> transactions;
    private final int totalCount;

    public LedgerListResponse(List<LedgerTransactionResponse> transactions, int totalCount) {
        this.transactions = transactions;
        this.totalCount = totalCount;
    }

    public List<LedgerTransactionResponse> getTransactions() {
        return transactions;
    }

    public int getTotalCount() {
        return totalCount;
    }
}
