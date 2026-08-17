package com.mayday.domain.mypage.dto;

import java.time.LocalDate;

public class MyPageSummaryResponse {

    private final String email;
    private final long recordedCount;
    private final long qualifiedEvidenceCount;
    private final long recognizedExpense;
    private final LocalDate taxDueDate;
    private final long taxDDay;

    public MyPageSummaryResponse(
            String email,
            long recordedCount,
            long qualifiedEvidenceCount,
            long recognizedExpense,
            LocalDate taxDueDate,
            long taxDDay
    ) {
        this.email = email;
        this.recordedCount = recordedCount;
        this.qualifiedEvidenceCount = qualifiedEvidenceCount;
        this.recognizedExpense = recognizedExpense;
        this.taxDueDate = taxDueDate;
        this.taxDDay = taxDDay;
    }

    public String getEmail() {
        return email;
    }

    public long getRecordedCount() {
        return recordedCount;
    }

    public long getQualifiedEvidenceCount() {
        return qualifiedEvidenceCount;
    }

    public long getRecognizedExpense() {
        return recognizedExpense;
    }

    public LocalDate getTaxDueDate() {
        return taxDueDate;
    }

    public long getTaxDDay() {
        return taxDDay;
    }
}
