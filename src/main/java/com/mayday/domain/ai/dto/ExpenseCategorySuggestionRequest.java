package com.mayday.domain.ai.dto;

import com.mayday.domain.ai.model.AnalysisSourceType;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExpenseCategorySuggestionRequest {

    private Long recordId;
    private AnalysisSourceType sourceType;
    private String rawText;
    private String ocrText;
    private LocalDate transactionDate;

    @PositiveOrZero(message = "금액은 0 이상이어야 합니다.")
    private Long amount;

    private String merchantName;
    private String itemName;
    private String businessType;

}
