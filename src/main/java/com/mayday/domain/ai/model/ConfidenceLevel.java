package com.mayday.domain.ai.model;

public enum ConfidenceLevel {
    HIGH("안심", "GREEN", 95, 100),
    MEDIUM("주의", "YELLOW", 70, 94),
    LOW("확인 필요", "RED", 0, 69);

    private final String label;
    private final String color;
    private final int minScore;
    private final int maxScore;

    ConfidenceLevel(String label, String color, int minScore, int maxScore) {
        this.label = label;
        this.color = color;
        this.minScore = minScore;
        this.maxScore = maxScore;
    }

    public static ConfidenceLevel fromScore(int score) {
        int normalizedScore = Math.max(0, Math.min(score, 100));
        if (normalizedScore >= HIGH.minScore) {
            return HIGH;
        }
        if (normalizedScore >= MEDIUM.minScore) {
            return MEDIUM;
        }
        return LOW;
    }

    public String getLabel() {
        return label;
    }

    public String getColor() {
        return color;
    }

    public int getMinScore() {
        return minScore;
    }

    public int getMaxScore() {
        return maxScore;
    }
}
