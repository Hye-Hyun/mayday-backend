package com.mayday.domain.expense.dto;

import com.mayday.domain.expense.model.TextInputTypeHint;

public class ExpenseTextParseResponse {

    private final String textInputId;
    private final String rawText;
    private final TextInputTypeHint typeHint;

    public ExpenseTextParseResponse(String textInputId, String rawText, TextInputTypeHint typeHint) {
        this.textInputId = textInputId;
        this.rawText = rawText;
        this.typeHint = typeHint;
    }

    public String getTextInputId() {
        return textInputId;
    }

    public String getRawText() {
        return rawText;
    }

    public TextInputTypeHint getTypeHint() {
        return typeHint;
    }
}
