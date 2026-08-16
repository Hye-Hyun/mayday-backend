package com.mayday.domain.expense.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class OcrResultData {
    private String ocrId;
    private String rawText;
    private String date;
    private String merchantName;
    private String itemName;
    private int income;
    private int expense;
}

