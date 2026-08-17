package com.mayday.domain.expense.dto;

import com.mayday.domain.expense.model.TextInputTypeHint;
import jakarta.validation.constraints.NotBlank;

public class ExpenseTextParseRequest {

    @NotBlank(message = "분석할 텍스트를 입력해주세요")
    private String rawText;

    private TextInputTypeHint typeHint = TextInputTypeHint.UNKNOWN;

    protected ExpenseTextParseRequest() {
    }

    public String getRawText() {
        return rawText;
    }

    public TextInputTypeHint getTypeHint() {
        return typeHint == null ? TextInputTypeHint.UNKNOWN : typeHint;
    }
}
