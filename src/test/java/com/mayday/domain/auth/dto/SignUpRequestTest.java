package com.mayday.domain.auth.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SignUpRequestTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void acceptsAgreementAliasesFromClientRequest() throws Exception {
        String json = """
                {
                  "email": "user@example.com",
                  "password": "password1",
                  "passwordConfirm": "password1",
                  "termsAgreed": true,
                  "privacyAgreed": true,
                  "evidenceProcessingAgreed": true
                }
                """;

        SignUpRequest request = objectMapper.readValue(json, SignUpRequest.class);

        assertThat(request.isAgreedToTerms()).isTrue();
        assertThat(request.isAgreedToPrivacy()).isTrue();
        assertThat(request.isAgreedToReceiptAnalysis()).isTrue();
    }
}
