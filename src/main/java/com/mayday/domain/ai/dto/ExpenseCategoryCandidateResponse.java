package com.mayday.domain.ai.dto;

import com.mayday.domain.ai.model.BusinessRelevance;
import com.mayday.domain.ai.model.ExpenseCategory;

public class ExpenseCategoryCandidateResponse {

    private final ExpenseCategory category;
    private final String categoryLabel;
    private final BusinessRelevance businessRelevance;
    private final String businessRelevanceLabel;
    private final int confidenceScore;
    private final String reason;

    public ExpenseCategoryCandidateResponse(
            ExpenseCategory category,
            BusinessRelevance businessRelevance,
            int confidenceScore,
            String reason
    ) {
        this.category = category;
        this.categoryLabel = category.getLabel();
        this.businessRelevance = businessRelevance;
        this.businessRelevanceLabel = businessRelevance.getLabel();
        this.confidenceScore = confidenceScore;
        this.reason = reason;
    }

    public ExpenseCategory getCategory() {
        return category;
    }

    public String getCategoryLabel() {
        return categoryLabel;
    }

    public BusinessRelevance getBusinessRelevance() {
        return businessRelevance;
    }

    public String getBusinessRelevanceLabel() {
        return businessRelevanceLabel;
    }

    public int getConfidenceScore() {
        return confidenceScore;
    }

    public String getReason() {
        return reason;
    }
}
