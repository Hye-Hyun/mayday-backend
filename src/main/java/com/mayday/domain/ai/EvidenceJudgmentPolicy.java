package com.mayday.domain.ai;

import com.mayday.domain.ai.model.EvidenceJudgment;
import com.mayday.domain.ai.model.EvidenceType;
import com.mayday.domain.ai.model.ExpenseCategory;

import java.util.Locale;

public final class EvidenceJudgmentPolicy {

    private static final long SMALL_AMOUNT_THRESHOLD = 30_000L;

    private EvidenceJudgmentPolicy() {
    }

    public static EvidenceJudgmentResult evaluate(
            String type,
            Long amount,
            EvidenceType evidenceType,
            ExpenseCategory category
    ) {
        return evaluate(type, amount, evidenceType, category != null ? category.name() : null);
    }

    public static EvidenceJudgmentResult evaluate(
            String type,
            Integer amount,
            String evidenceType,
            String category
    ) {
        Long normalizedAmount = amount != null ? amount.longValue() : null;
        return evaluate(type, normalizedAmount, parseEvidenceType(evidenceType), category);
    }

    private static EvidenceJudgmentResult evaluate(
            String type,
            Long amount,
            EvidenceType evidenceType,
            String category
    ) {
        if (!"EXPENSE".equalsIgnoreCase(nullToBlank(type))) {
            return new EvidenceJudgmentResult(
                    EvidenceJudgment.REVIEW_REQUIRED,
                    false,
                    false,
                    "수입 기록은 적격증빙 판단 대상이 아니므로 별도 확인이 필요합니다."
            );
        }

        if (amount == null || amount <= 0) {
            return new EvidenceJudgmentResult(
                    EvidenceJudgment.REVIEW_REQUIRED,
                    false,
                    false,
                    "금액을 확인할 수 없어 증빙 및 경비 처리 가능성 판단이 필요합니다."
            );
        }

        boolean qualifiedByEvidenceType = isQualifiedEvidenceType(evidenceType);
        boolean qualifiedBySmallAmount = amount <= SMALL_AMOUNT_THRESHOLD;
        boolean qualifiedEvidence = qualifiedByEvidenceType || qualifiedBySmallAmount;
        boolean expenseTreatmentPossible = isExpenseTreatmentPossible(category);

        if (qualifiedByEvidenceType) {
            return new EvidenceJudgmentResult(
                    EvidenceJudgment.QUALIFIED,
                    true,
                    expenseTreatmentPossible,
                    evidenceType.getLabel() + " 유형으로 확인되어 적격증빙 후보로 분류했습니다."
            );
        }

        if (qualifiedBySmallAmount) {
            return new EvidenceJudgmentResult(
                    EvidenceJudgment.QUALIFIED,
                    true,
                    expenseTreatmentPossible,
                    "지출 금액이 3만 원 이하이므로 적격증빙 후보로 분류했습니다. 최종 처리는 사용자 확인이 필요합니다."
            );
        }

        return new EvidenceJudgmentResult(
                EvidenceJudgment.NON_QUALIFIED,
                false,
                expenseTreatmentPossible,
                "세금계산서, 계산서, 신용카드 매출전표, 현금영수증 단서를 찾지 못해 부적격증빙 후보로 분류했습니다."
        );
    }

    private static boolean isQualifiedEvidenceType(EvidenceType evidenceType) {
        return evidenceType == EvidenceType.CARD_RECEIPT
                || evidenceType == EvidenceType.CASH_RECEIPT
                || evidenceType == EvidenceType.TAX_INVOICE
                || evidenceType == EvidenceType.INVOICE;
    }

    private static boolean isExpenseTreatmentPossible(String category) {
        String normalizedCategory = nullToBlank(category).toUpperCase(Locale.ROOT);
        return !normalizedCategory.isBlank()
                && !"OTHER_EXPENSE".equals(normalizedCategory)
                && !"OTHER_INCOME".equals(normalizedCategory)
                && !"SALES".equals(normalizedCategory);
    }

    private static EvidenceType parseEvidenceType(String evidenceType) {
        try {
            return EvidenceType.from(evidenceType);
        } catch (IllegalArgumentException e) {
            return EvidenceType.NON_QUALIFIED;
        }
    }

    private static String nullToBlank(String value) {
        return value == null ? "" : value;
    }
}
