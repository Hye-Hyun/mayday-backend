package com.mayday.domain.ai;

import com.mayday.domain.ai.dto.ExpenseCategoryCandidateResponse;
import com.mayday.domain.ai.dto.ExpenseCategorySuggestionRequest;
import com.mayday.domain.ai.dto.ExpenseCategorySuggestionResponse;
import com.mayday.domain.ai.model.BusinessRelevance;
import com.mayday.domain.ai.model.ExpenseCategory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class ExpenseCategorySuggestionService {

    private static final long LARGE_EXPENSE_THRESHOLD = 1_000_000L;

    public ExpenseCategorySuggestionResponse suggest(Long userId, ExpenseCategorySuggestionRequest request) {
        validateRequest(request);

        String analysisText = buildAnalysisText(request);
        ExpenseCategory category = suggestCategory(analysisText, request);
        BusinessRelevance relevance = suggestBusinessRelevance(analysisText, request);
        int confidenceScore = calculateConfidenceScore(category, relevance, analysisText, request);

        ExpenseCategoryCandidateResponse primarySuggestion = new ExpenseCategoryCandidateResponse(
                category,
                relevance,
                confidenceScore,
                buildReason(category, relevance, analysisText, request)
        );

        List<ExpenseCategoryCandidateResponse> candidates = new ArrayList<>();
        candidates.add(primarySuggestion);
        addFallbackCandidate(candidates, primarySuggestion);

        return new ExpenseCategorySuggestionResponse(
                request.getRecordId(),
                primarySuggestion,
                candidates
        );
    }

    private void validateRequest(ExpenseCategorySuggestionRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("분석할 거래 정보가 필요합니다.");
        }

        boolean hasText = hasText(request.getRawText()) || hasText(request.getOcrText());
        boolean hasField = hasText(request.getMerchantName())
                || hasText(request.getItemName())
                || request.getAmount() != null
                || request.getTransactionDate() != null;

        if (!hasText && !hasField) {
            throw new IllegalArgumentException("분석할 텍스트 또는 거래 필드가 필요합니다.");
        }
    }

    private String buildAnalysisText(ExpenseCategorySuggestionRequest request) {
        return String.join(" ",
                nullToBlank(request.getRawText()),
                nullToBlank(request.getOcrText()),
                nullToBlank(request.getMerchantName()),
                nullToBlank(request.getItemName()),
                nullToBlank(request.getBusinessType())
        ).toLowerCase(Locale.ROOT);
    }

    private ExpenseCategory suggestCategory(String text, ExpenseCategorySuggestionRequest request) {
        if (request.getAmount() != null && request.getAmount() > LARGE_EXPENSE_THRESHOLD) {
            return ExpenseCategory.OTHER_EXPENSE;
        }
        if (containsAny(text, "문구", "사무용품", "복사용지", "토너", "잉크", "노트", "소모품")) {
            return ExpenseCategory.SUPPLIES;
        }
        if (containsAny(text, "수수료", "플랫폼", "결제대행", "pg", "송금", "서비스 이용료")) {
            return ExpenseCategory.SERVICE_FEES;
        }
        if (containsAny(text, "택시", "지하철", "버스", "기차", "ktx", "항공", "숙박", "주차", "교통")) {
            return ExpenseCategory.TRAVEL_AND_TRANSPORTATION;
        }
        if (containsAny(text, "광고", "마케팅", "홍보", "인스타", "페이스북", "구글애즈", "네이버광고")) {
            return ExpenseCategory.ADVERTISING_EXPENSE;
        }
        if (containsAny(text, "임대료", "월세", "공유오피스", "사무실", "스튜디오", "코워킹")) {
            return ExpenseCategory.RENT;
        }
        if (containsAny(text, "택배", "배송", "퀵", "운송", "화물")) {
            return ExpenseCategory.DELIVERY_EXPENSE;
        }
        if (containsAny(text, "접대", "미팅", "회의", "식대", "카페", "커피", "식당")) {
            return ExpenseCategory.BUSINESS_PROMOTION_EXPENSE;
        }
        if (containsAny(text, "세금", "부가세", "인지세", "등록면허세", "공과금")) {
            return ExpenseCategory.TAXES_AND_DUES;
        }
        if (containsAny(text, "주유", "충전", "하이패스", "정비", "세차", "차량")) {
            return ExpenseCategory.VEHICLE_MAINTENANCE;
        }

        return ExpenseCategory.OTHER_EXPENSE;
    }

    private BusinessRelevance suggestBusinessRelevance(String text, ExpenseCategorySuggestionRequest request) {
        if (containsAny(text, "업무", "프로젝트", "거래처", "클라이언트", "촬영", "제작", "외주")) {
            return BusinessRelevance.HIGH;
        }
        if (containsAny(text, "개인", "생활", "마트", "편의점", "영화", "병원", "약국")) {
            return BusinessRelevance.LOW;
        }
        if (request.getBusinessType() != null && !request.getBusinessType().isBlank()) {
            return BusinessRelevance.MEDIUM;
        }

        return BusinessRelevance.MEDIUM;
    }

    private int calculateConfidenceScore(
            ExpenseCategory category,
            BusinessRelevance relevance,
            String text,
            ExpenseCategorySuggestionRequest request
    ) {
        int score = 60;

        if (category != ExpenseCategory.OTHER_EXPENSE) {
            score += 20;
        }
        if (relevance == BusinessRelevance.HIGH || relevance == BusinessRelevance.LOW) {
            score += 10;
        }
        if (hasText(request.getMerchantName()) || hasText(request.getItemName())) {
            score += 5;
        }
        if (request.getAmount() != null && request.getAmount() > LARGE_EXPENSE_THRESHOLD) {
            score = Math.min(score, 70);
        }
        if (text.length() < 8) {
            score -= 15;
        }

        return Math.max(30, Math.min(score, 95));
    }

    private String buildReason(
            ExpenseCategory category,
            BusinessRelevance relevance,
            String text,
            ExpenseCategorySuggestionRequest request
    ) {
        if (request.getAmount() != null && request.getAmount() > LARGE_EXPENSE_THRESHOLD) {
            return "지출 금액이 100만원을 초과해 우선 기타(비용)으로 분류하고 추가 확인이 필요합니다.";
        }
        if (category == ExpenseCategory.OTHER_EXPENSE) {
            return "거래 텍스트에서 명확한 경비 항목 단서를 찾지 못해 기타(비용)으로 제안합니다.";
        }
        if (relevance == BusinessRelevance.LOW) {
            return "거래처 또는 내용에 개인 지출 가능성이 있는 단어가 포함되어 업무 관련성을 낮게 제안합니다.";
        }

        return category.getLabel() + "에 가까운 단어가 확인되어 해당 경비 항목으로 제안합니다.";
    }

    private void addFallbackCandidate(
            List<ExpenseCategoryCandidateResponse> candidates,
            ExpenseCategoryCandidateResponse primarySuggestion
    ) {
        if (primarySuggestion.getCategory() != ExpenseCategory.OTHER_EXPENSE) {
            candidates.add(new ExpenseCategoryCandidateResponse(
                    ExpenseCategory.OTHER_EXPENSE,
                    BusinessRelevance.MEDIUM,
                    Math.max(30, primarySuggestion.getConfidenceScore() - 25),
                    "거래 성격이 불명확한 경우를 대비한 보수적 대안입니다."
            ));
        }
    }

    private boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword.toLowerCase(Locale.ROOT))) {
                return true;
            }
        }
        return false;
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private String nullToBlank(String value) {
        return value == null ? "" : value;
    }
}
