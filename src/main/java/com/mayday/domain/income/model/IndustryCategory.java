package com.mayday.domain.income.model;

public enum IndustryCategory {

    AGRICULTURE_WHOLESALE("농업·임업·어업, 도소매업 등", 300_000_000L),
    MANUFACTURING_FOOD("제조업, 숙박·음식점업 등", 150_000_000L),
    SERVICE("서비스업 등", 75_000_000L);

    private final String label;
    private final Long standardAmount;

    IndustryCategory(String label, Long standardAmount) {
        this.label = label;
        this.standardAmount = standardAmount;
    }

    public String getLabel() { return label; }
    public Long getStandardAmount() { return standardAmount; }
}