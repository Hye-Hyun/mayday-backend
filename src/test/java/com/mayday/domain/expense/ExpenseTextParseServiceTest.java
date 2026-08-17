package com.mayday.domain.expense;

import com.mayday.domain.expense.dto.ExpenseTextParseRequest;
import com.mayday.domain.expense.dto.ExpenseTextParseResponse;
import com.mayday.domain.expense.model.TextInputTypeHint;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class ExpenseTextParseServiceTest {

    private static final long USER_ID = 1L;

    private final ExpenseTextParseService expenseTextParseService =
            new ExpenseTextParseService(new ReceiptTextParser());

    @Test
    void parseReturnsTextInputForAiRetry() {
        ExpenseTextParseRequest request = BeanUtils.instantiateClass(ExpenseTextParseRequest.class);
        ReflectionTestUtils.setField(request, "rawText", "08/10 알파문구 15,000원 결제");
        ReflectionTestUtils.setField(request, "typeHint", TextInputTypeHint.EXPENSE);

        ExpenseTextParseResponse response = expenseTextParseService.parse(USER_ID, request);

        assertThat(response.getTextInputId()).startsWith("txt_");
        assertThat(response.getRawText()).isEqualTo("08/10 알파문구 15,000원 결제");
        assertThat(response.getTypeHint()).isEqualTo(TextInputTypeHint.EXPENSE);
    }

    @Test
    void parseDefaultsMissingTypeHintToUnknown() {
        ExpenseTextParseRequest request = BeanUtils.instantiateClass(ExpenseTextParseRequest.class);
        ReflectionTestUtils.setField(request, "rawText", "입금 967,000원");
        ReflectionTestUtils.setField(request, "typeHint", null);

        ExpenseTextParseResponse response = expenseTextParseService.parse(USER_ID, request);

        assertThat(response.getTypeHint()).isEqualTo(TextInputTypeHint.UNKNOWN);
    }
}
