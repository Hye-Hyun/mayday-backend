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
              "itemName": "string",
              "amount": number,
              "category": "SUPPLIES, MEAL, TRANSPORT, TAX 등",
              "evidenceType": "CARD_RECEIPT, CASH_RECEIPT, TAX_INVOICE, NONE 등",
              "qualifiedEvidence": boolean,
              "reason": "판단 이유를 한국어로",
              "confidenceScore": 0에서 100 사이 정수
            }

            판단 시 유의사항:
            - 세무 판단을 확정하지 말고 참고용으로만 판단할 것.
            - 지출 금액이 3만 원 이하이면 적격증빙 키워드가 없어도 적격 후보로 볼 수 있음.
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
            throw new LlmAnalysisException("AI 분석 결과 파싱에 실패했습니다.");
        }
    }
}
