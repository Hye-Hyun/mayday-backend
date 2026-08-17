package com.mayday.domain.income.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public class IncomeCreateRequest {

    @NotNull(message = "금액을 입력해주세요.")
    @Positive(message = "금액은 0보다 커야 합니다.")
    private Long amount;

    @NotNull(message = "날짜를 입력해주세요.")
    private LocalDate date;

    protected IncomeCreateRequest() {
    }

    public Long getAmount() { return amount; }
    public LocalDate getDate() { return date; }
}