package com.mayday.domain.user;

public enum JobCategory {
    SALES_ORIENTED(60_000_000L),           // 판매 중심
    SPACE_OR_PRODUCTION_ORIENTED(36_000_000L), // 공간 운영·직접 제작 중심
    SKILL_PLATFORM_ORIENTED(24_000_000L);   // 기술·재능·플랫폼 노동 중심

    private final long standardAmount;

    JobCategory(long standardAmount) {
        this.standardAmount = standardAmount;
    }

    public long getStandardAmount() {
        return standardAmount;
    }
}
