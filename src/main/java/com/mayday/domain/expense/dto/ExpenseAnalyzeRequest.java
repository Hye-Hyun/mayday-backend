package com.mayday.domain.expense.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ExpenseAnalyzeRequest {

    private String sourceType;
    private String sourceId;

    @NotBlank(message = "분석할 텍스트를 입력해주세요.")
    private String rawText;

    private boolean withholdingTaxApplied;
}
