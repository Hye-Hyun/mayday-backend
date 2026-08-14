package com.mayday.domain.expense;

import lombok.Builder;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ReceiptTextParser {

    private static final Pattern DATE_PATTERN =
            Pattern.compile("(\\d{4})[-.년]\\s?(\\d{1,2})[-.월]\\s?(\\d{1,2})일?");
    private static final Pattern AMOUNT_PATTERN =
            Pattern.compile("([\\d,]{4,})\\s?원");

    public ParsedReceipt parse(String rawText) {
        return ParsedReceipt.builder()
                .date(extractDate(rawText))
                .merchantName(extractMerchantName(rawText))
                .itemName(buildItemName(extractMerchantName(rawText)))
                .expense(extractAmount(rawText))
                .build();
    }

    private String extractDate(String text) {
        Matcher m = DATE_PATTERN.matcher(text);
        if (m.find()) {
            String month = String.format("%02d", Integer.parseInt(m.group(2)));
            String day = String.format("%02d", Integer.parseInt(m.group(3)));
            return m.group(1) + "-" + month + "-" + day;
        }
        return null;
    }

    private int extractAmount(String text) {
        Matcher m = AMOUNT_PATTERN.matcher(text);
        int maxAmount = 0;
        while (m.find()) {
            try {
                maxAmount = Math.max(maxAmount, Integer.parseInt(m.group(1).replace(",", "")));
            } catch (NumberFormatException ignored) {
            }
        }
        return maxAmount;
    }

    private String extractMerchantName(String text) {
        for (String line : text.split("\\n")) {
            String trimmed = line.trim();
            if (!trimmed.isEmpty()
                    && !DATE_PATTERN.matcher(trimmed).find()
                    && !AMOUNT_PATTERN.matcher(trimmed).find()) {
                return trimmed;
            }
        }
        return null;
    }

    private String buildItemName(String merchantName) {
        return merchantName != null ? merchantName + " 구매" : "품목 확인 필요";
    }

    @Getter
    @Builder
    public static class ParsedReceipt {
        private String date;
        private String merchantName;
        private String itemName;
        private int expense;
    }
}
