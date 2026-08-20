package com.mayday.domain.income.dto;

import java.time.OffsetDateTime;

public class IncomeUpdateResponse {

    private final String incomeId;
    private final OffsetDateTime updatedAt;

    public IncomeUpdateResponse(String incomeId, OffsetDateTime updatedAt) {
        this.incomeId = incomeId;
        this.updatedAt = updatedAt;
    }

    public String getIncomeId() { return incomeId; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
}