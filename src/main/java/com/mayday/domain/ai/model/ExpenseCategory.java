package com.mayday.domain.ai.model;

public enum ExpenseCategory {
    SUPPLIES("소모품비", CategoryType.EXPENSE),
    SERVICE_FEES("지급수수료", CategoryType.EXPENSE),
    TRAVEL_AND_TRANSPORTATION("여비교통비", CategoryType.EXPENSE),
    ADVERTISING_EXPENSE("광고선전비", CategoryType.EXPENSE),
    RENT("임차료", CategoryType.EXPENSE),
    DELIVERY_EXPENSE("운반비", CategoryType.EXPENSE),
    BUSINESS_PROMOTION_EXPENSE("기업업무추진비", CategoryType.EXPENSE),
    TAXES_AND_DUES("제세공과금", CategoryType.EXPENSE),
    VEHICLE_MAINTENANCE("차량유지비", CategoryType.EXPENSE),
    OTHER_EXPENSE("기타(비용)", CategoryType.EXPENSE),
    SALES("매출", CategoryType.INCOME),
    OTHER_INCOME("기타(수입)", CategoryType.INCOME);

    private final String label;
    private final CategoryType type;

    ExpenseCategory(String label, CategoryType type) {
        this.label = label;
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    public boolean isExpense() {
        return type == CategoryType.EXPENSE;
    }

    public boolean isIncome() {
        return type == CategoryType.INCOME;
    }

    private enum CategoryType {
        EXPENSE,
        INCOME
    }
}
