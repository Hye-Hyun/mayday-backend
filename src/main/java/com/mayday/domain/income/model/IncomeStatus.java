package com.mayday.domain.income.model;

public enum IncomeStatus {
    SAFE("여유"),
    NEAR("근접"),
    REACHED("도달"),
    EXCEEDED("초과");

    private final String label;

    IncomeStatus(String label) {
        this.label = label;
    }

    public String getLabel() { return label; }
}