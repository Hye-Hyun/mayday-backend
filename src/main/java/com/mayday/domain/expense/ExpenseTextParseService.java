package com.mayday.domain.expense;

import com.mayday.domain.expense.dto.ExpenseTextParseRequest;
import com.mayday.domain.expense.dto.ExpenseTextParseResponse;
import com.mayday.domain.expense.model.TextInputTypeHint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExpenseTextParseService {

    private final ReceiptTextParser receiptTextParser;

    public ExpenseTextParseResponse parse(Long userId, ExpenseTextParseRequest request) {
        ReceiptTextParser.ParsedReceipt parsed = receiptTextParser.parse(request.getRawText());
        TextInputTypeHint typeHint = request.getTypeHint();

        log.info(
                "[TEXT_INPUT_RETRY_LOG] userId={}, typeHint={}, rawText={}, parsedDate={}, parsedMerchantName={}, parsedAmount={}",
                userId,
                typeHint,
                request.getRawText(),
                parsed.getDate(),
                parsed.getMerchantName(),
                parsed.getExpense()
        );

        return new ExpenseTextParseResponse(
                generateTextInputId(),
                request.getRawText(),
                typeHint
        );
    }

    private String generateTextInputId() {
        return "txt_" + UUID.randomUUID().toString().substring(0, 8);
    }
}
