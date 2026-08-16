package com.mayday.domain.user;

public enum JobCategory {
    SALES_ORIENTED(60_000_000L),
    SPACE_OR_PRODUCTION_ORIENTED(36_000_000L),
    SKILL_PLATFORM_ORIENTED(24_000_000L);

    private final long standardAmount;

    JobCategory(long standardAmount) {
        this.standardAmount = standardAmount;
    }

    public long getStandardAmount() {
        return standardAmount;
    }
}
