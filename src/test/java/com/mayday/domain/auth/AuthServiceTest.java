package com.mayday.domain.auth;

import com.mayday.domain.auth.dto.AuthResponse;
import com.mayday.domain.user.JobCategory;
import com.mayday.domain.user.User;
import com.mayday.domain.user.UserRepository;
import com.mayday.global.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthServiceTest {

    private static final String DEMO_EMAIL = "demo@mayday.local";
    private static final String DEMO_PASSWORD = "demo-password";

    private final UserRepository userRepository = mock(UserRepository.class);
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder =
            mock(org.springframework.security.crypto.password.PasswordEncoder.class);
    private final JwtTokenProvider jwtTokenProvider = mock(JwtTokenProvider.class);

    @Test
    void demoLoginReturnsNotFoundWhenDisabled() {
        AuthService authService = authService(false);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                authService::demoLogin
        );

        assertEquals(404, exception.getStatusCode().value());
        verify(userRepository, never()).findByEmail(any());
    }

    @Test
    void demoLoginCreatesOnboardedDemoUserAndReturnsToken() {
        AuthService authService = authService(true);
        when(userRepository.findByEmail(DEMO_EMAIL)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(DEMO_PASSWORD)).thenReturn("encoded-demo-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtTokenProvider.createToken(null, DEMO_EMAIL)).thenReturn("demo-access-token");

        AuthResponse response = authService.demoLogin();

        assertEquals("demo-access-token", response.getAccessToken());
        assertEquals(DEMO_EMAIL, response.getEmail());
        assertTrue(response.isOnboardingCompleted());
        verify(userRepository).save(org.mockito.ArgumentMatchers.argThat(user ->
                user.isOnboardingCompleted()
                        && user.getJobCategory() == JobCategory.SALES_ORIENTED
                        && user.getInitialIncome() == 0L
        ));
    }

    @Test
    void demoLoginReusesExistingDemoUser() {
        AuthService authService = authService(true);
        User existingUser = new User(DEMO_EMAIL, "encoded-password", true, true, true);
        existingUser.completeOnboarding(JobCategory.SKILL_PLATFORM_ORIENTED, 100L);
        when(userRepository.findByEmail(DEMO_EMAIL)).thenReturn(Optional.of(existingUser));
        when(jwtTokenProvider.createToken(null, DEMO_EMAIL)).thenReturn("existing-user-token");

        AuthResponse response = authService.demoLogin();

        assertEquals("existing-user-token", response.getAccessToken());
        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
    }

    private AuthService authService(boolean demoLoginEnabled) {
        return new AuthService(
                userRepository,
                passwordEncoder,
                jwtTokenProvider,
                demoLoginEnabled,
                DEMO_EMAIL,
                DEMO_PASSWORD
        );
    }
}
