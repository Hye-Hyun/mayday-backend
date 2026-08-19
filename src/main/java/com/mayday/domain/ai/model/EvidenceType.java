package com.mayday.domain.ai.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public enum EvidenceType {

    CARD_RECEIPT("카드 매출전표"),
    CASH_RECEIPT("현금영수증"),
    TAX_INVOICE("세금계산서"),
    INVOICE("계산서"),
    NON_QUALIFIED("해당 없음"),
    @Deprecated
    SIMPLE_RECEIPT("간이영수증"),
    @Deprecated
    UNKNOWN("확인 필요");

    private final String label;

    EvidenceType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @JsonValue
    public String getValue() {
        if (this == SIMPLE_RECEIPT || this == UNKNOWN) {
            return NON_QUALIFIED.name();
        }
        return name();
    }

    public boolean isSelectable() {
        return this != SIMPLE_RECEIPT && this != UNKNOWN;
    }

    public static List<EvidenceType> selectableValues() {
        return Arrays.stream(values())
                .filter(EvidenceType::isSelectable)
                .toList();
    }

    @JsonCreator
    public static EvidenceType from(String value) {
        String normalized = value == null ? "" : value.trim().toUpperCase(Locale.ROOT);
        if (normalized.isBlank()
                || "NONE".equals(normalized)
                || "UNKNOWN".equals(normalized)
                || "SIMPLE_RECEIPT".equals(normalized)
                || "NON_QUALIFIED".equals(normalized)) {
            return NON_QUALIFIED;
        }

        return EvidenceType.valueOf(normalized);
    }
}
