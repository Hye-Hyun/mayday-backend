package com.mayday.domain.expense;

import lombok.Builder;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.time.Year;

@Component
public class ReceiptTextParser {

    private static final Pattern DATE_PATTERN =
            Pattern.compile("(\\d{4})[-.년]\\s?(\\d{1,2})[-.월]\\s?(\\d{1,2})일?");
    private static final Pattern SHORT_DATE_PATTERN =
            Pattern.compile("(\\d{1,2})/(\\d{1,2})");
    private static final Pattern AMOUNT_PATTERN =
            Pattern.compile("([\\d,]{4,})\\s?원");

    public ParsedReceipt parse(String rawText) {
        String merchantName = extractMerchantName(rawText);

        return ParsedReceipt.builder()
                .date(extractDate(rawText))
                .merchantName(merchantName)
                .itemName(buildItemName(merchantName))
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

        Matcher shortDateMatcher = SHORT_DATE_PATTERN.matcher(text);
        if (shortDateMatcher.find()) {
            String month = String.format("%02d", Integer.parseInt(shortDateMatcher.group(1)));
            String day = String.format("%02d", Integer.parseInt(shortDateMatcher.group(2)));
            return Year.now().getValue() + "-" + month + "-" + day;
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
            String merchantName = sanitizeMerchantName(line);
            if (!merchantName.isEmpty()) {
                return merchantName;
            }
        }
        return null;
    }

    private String sanitizeMerchantName(String line) {
        String merchantName = DATE_PATTERN.matcher(line).replaceAll(" ");
        merchantName = SHORT_DATE_PATTERN.matcher(merchantName).replaceAll(" ");
        merchantName = AMOUNT_PATTERN.matcher(merchantName).replaceAll(" ");
        merchantName = merchantName
                .replace("결제", " ")
                .replace("승인", " ")
                .replace("구매", " ")
                .trim();

        return merchantName.replaceAll("\\s+", " ");
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
