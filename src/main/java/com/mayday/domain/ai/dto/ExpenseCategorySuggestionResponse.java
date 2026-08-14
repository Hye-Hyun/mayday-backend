package com.mayday.domain.ai.dto;

import java.util.List;

public class ExpenseCategorySuggestionResponse {

    private final Long recordId;
    private final ExpenseCategoryCandidateResponse primarySuggestion;
    private final List<ExpenseCategoryCandidateResponse> candidates;

    public ExpenseCategorySuggestionResponse(
            Long recordId,
            ExpenseCategoryCandidateResponse primarySuggestion,
            List<ExpenseCategoryCandidateResponse> candidates
    ) {
        this.recordId = recordId;
        this.primarySuggestion = primarySuggestion;
        this.candidates = candidates;
    }

    public Long getRecordId() {
        return recordId;
    }

    public ExpenseCategoryCandidateResponse getPrimarySuggestion() {
        return primarySuggestion;
    }

    public List<ExpenseCategoryCandidateResponse> getCandidates() {
        return candidates;
    }
}
