package com.mayday.domain.expense.dto;

import com.mayday.domain.ai.model.EvidenceType;
import com.mayday.domain.ai.model.ExpenseCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExpenseUpdateRequest {

    @NotNull(message = "거래 일자를 입력해주세요.")
    private LocalDate date;

    private String merchantName;

    @NotBlank(message = "거래 내용을 입력해주세요.")
    private String itemName;

    @NotNull(message = "거래 금액을 입력해주세요.")
    @Positive(message = "거래 금액은 0보다 커야 합니다")
    private Long amount;

    @NotNull(message = "거래 항목을 선택해주세요.")
    private ExpenseCategory category;

    @NotNull(message = "증빙 유형을 선택해주세요.")
    private EvidenceType evidenceType;

    @NotNull(message = "적격 증빙 여부를 선택해주세요.")
    private Boolean qualifiedEvidence;

    private String remark;
}
