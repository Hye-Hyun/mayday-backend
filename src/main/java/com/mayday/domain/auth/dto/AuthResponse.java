package com.mayday.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponse {

    private String accessToken;
    private String tokenType;
    private Long userId;
    private String email;
    private boolean onboardingCompleted;
}
