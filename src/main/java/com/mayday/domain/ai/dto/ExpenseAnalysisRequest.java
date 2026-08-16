package com.mayday.domain.ai.dto;

import com.mayday.domain.ai.model.AnalysisSourceType;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExpenseAnalysisRequest {

    private AnalysisSourceType sourceType;

    private String sourceId;

    @NotBlank(message = "분석할 텍스트를 입력해주세요.")
    private String rawText;

    private Boolean withholdingTaxApplied;
}
