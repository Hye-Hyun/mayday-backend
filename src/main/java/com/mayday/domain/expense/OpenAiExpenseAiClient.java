package com.mayday.domain.expense;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mayday.domain.expense.dto.ExpenseAiRawResult;
import com.mayday.global.exception.LlmAnalysisException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OpenAiExpenseAiClient implements ExpenseAiClient {

    private final WebClient llmWebClient;
    private final ObjectMapper objectMapper;

    private static final String SYSTEM_PROMPT = """
            너는 지출/수입 증빙 텍스트를 분석하는 어시스턴트다.
            아래 JSON 스키마와 동일한 키를 가진 JSON만 반환하라. 다른 텍스트는 절대 포함하지 마라.

            {
              "type": "EXPENSE 또는 INCOME",
              "date": "YYYY-MM-DD",
              "merchantName": "string",
              "itemName": "거래 내용. 확인 불가하면 '-'",
              "amount": number,
              "category": "SUPPLIES, SERVICE_FEES, TRAVEL_AND_TRANSPORTATION, ADVERTISING_EXPENSE, RENT, DELIVERY_EXPENSE, BUSINESS_PROMOTION_EXPENSE, TAXES_AND_DUES, VEHICLE_MAINTENANCE, OTHER_EXPENSE, SALES, OTHER_INCOME 중 하나",
              "evidenceType": "CARD_RECEIPT, CASH_RECEIPT, TAX_INVOICE, INVOICE, NON_QUALIFIED 중 하나",
              "qualifiedEvidence": boolean,
              "reason": "판단 이유를 한국어로",
              "confidenceScore": 0에서 100 사이 정수
            }

            판단 시 유의사항:
            - 세무 판단을 확정하지 말고 참고용으로만 판단할 것.
            - 적격증빙은 CARD_RECEIPT, CASH_RECEIPT, TAX_INVOICE, INVOICE 네 가지로만 판단할 것.
            - 지출 금액이 3만 원 이하이면 적격증빙 키워드가 없어도 적격 후보로 볼 수 있음.
            - 그 외 증빙 단서가 없으면 evidenceType은 NON_QUALIFIED, qualifiedEvidence는 false로 반환할 것.
            - 실제 거래 내용이 확인되지 않으면 itemName은 "string" 같은 예시값이 아니라 "-"로 반환할 것.
            """;

    @Override
    public ExpenseAiRawResult analyze(String rawText) {
        Map<String, Object> requestBody = Map.of(
                "model", "gpt-4o-mini",
                "response_format", Map.of("type", "json_object"),
                "messages", List.of(
                        Map.of("role", "system", "content", SYSTEM_PROMPT),
                        Map.of("role", "user", "content", rawText)
                )
        );

        Map<String, Object> response = llmWebClient.post()
                .uri("/chat/completions")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        try {
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            String content = (String) message.get("content");
            return objectMapper.readValue(content, ExpenseAiRawResult.class);
        } catch (Exception e) {
            throw new LlmAnalysisException("AI 분석에 실패했습니다. 잠시 후 다시 시도해주세요");
        }
    }
}
