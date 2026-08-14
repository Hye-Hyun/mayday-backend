package com.mayday.domain.user.dto;

import com.mayday.domain.user.JobCategory;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record OnboardingRequest(
        @NotNull(message = "업종군을 선택해주세요.")
        JobCategory jobCategory,

        @NotNull(message = "초기 부수입을 입력해주세요.")
        @PositiveOrZero(message = "부수입은 0 이상이어야 합니다.")
        Long initialIncome
) {}