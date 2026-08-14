package com.mayday.domain.ai.model;

public enum ExpenseCategory {
    SUPPLIES("소모품비"),
    SERVICE_FEES("지급수수료"),
    TRAVEL_AND_TRANSPORTATION("여비교통비"),
    ADVERTISING_EXPENSE("광고선전비"),
    RENT("임차료"),
    DELIVERY_EXPENSE("운반비"),
    BUSINESS_PROMOTION_EXPENSE("기업업무추진비"),
    TAXES_AND_DUES("제세공과금"),
    VEHICLE_MAINTENANCE("차량유지비"),
    OTHER_EXPENSE("기타(비용)");

    private final String label;

    ExpenseCategory(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
