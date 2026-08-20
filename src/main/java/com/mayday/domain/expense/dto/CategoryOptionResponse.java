package com.mayday.domain.expense.dto;

public class CategoryOptionResponse {

    private final String label;
    private final String value;

    public CategoryOptionResponse(String label, String value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() { return label; }

    public String getValue() { return value; }
}
