package com.mayday.domain.ai.model;

public enum EvidenceType {

    CARD_RECEIPT("카드 매출전표"),
    CASH_RECEIPT("현금영수증"),
    TAX_INVOICE("세금계산서"),
    INVOICE("계산서"),
    SIMPLE_RECEIPT("간이영수증"),
    UNKNOWN("확인 필요");

    private final String label;

    EvidenceType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
