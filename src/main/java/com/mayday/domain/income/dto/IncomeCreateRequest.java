package com.mayday.domain.income.dto;

import com.mayday.domain.ai.model.ExpenseCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public class IncomeCreateRequest {

    private static final double WITHHOLDING_TAX_RATE = 0.967;

    private String analysisId;

    @NotNull(message = "날짜를 입력해주세요.")
    private LocalDate date;

    private String merchantName;

    @NotBlank(message = "거래 내용을 입력해주세요.")
    private String itemName;

    @NotNull(message = "금액을 입력해주세요.")
    @Positive(message = "수입 금액은 0보다 커야 합니다")
    private Long amount;

    @Positive(message = "실수령 금액은 0보다 커야 합니다")
    private Long receivedAmount;

    private boolean withholdingTaxApplied;

    @NotNull(message = "수입 계정과목을 선택해주세요.")
    private ExpenseCategory category;

    private String remark;

    protected IncomeCreateRequest() {
    }

    public Long resolveReceivedAmount() {
        if (receivedAmount != null) {
            return receivedAmount;
        }
        if (withholdingTaxApplied) {
            return Math.round(amount * WITHHOLDING_TAX_RATE);
        }
        return amount;
    }

    public String getAnalysisId() { return analysisId; }
    public LocalDate getDate() { return date; }
    public String getMerchantName() { return merchantName; }
    public String getItemName() { return itemName; }
    public Long getAmount() { return amount; }
    public Long getReceivedAmount() { return receivedAmount; }
    public boolean isWithholdingTaxApplied() { return withholdingTaxApplied; }
    public ExpenseCategory getCategory() { return category; }
    public String getRemark() { return remark; }
}
