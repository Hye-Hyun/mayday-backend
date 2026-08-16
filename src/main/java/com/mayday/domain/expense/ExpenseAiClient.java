package com.mayday.domain.expense;

import com.mayday.domain.expense.dto.ExpenseAiRawResult;

public interface ExpenseAiClient {
    ExpenseAiRawResult analyze(String rawText);
}
