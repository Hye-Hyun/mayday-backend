package com.mayday.domain.ai.model;

public enum BusinessRelevance {
    HIGH("높음"),
    MEDIUM("보통"),
    LOW("낮음");

    private final String label;

    BusinessRelevance(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
