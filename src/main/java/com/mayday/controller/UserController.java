package com.mayday.controller;

import com.mayday.domain.user.UserService;
import com.mayday.domain.user.dto.OnboardingRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PatchMapping("/me/onboarding")
    public ResponseEntity<Void> completeOnboarding(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody OnboardingRequest request
    ) {
        userService.completeOnboarding(userId, request);
        return ResponseEntity.ok().build();
    }
}