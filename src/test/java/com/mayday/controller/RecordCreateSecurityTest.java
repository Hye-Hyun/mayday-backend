package com.mayday.controller;

import com.mayday.domain.user.User;
import com.mayday.domain.user.UserRepository;
import com.mayday.global.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RecordCreateSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Test
    void createsIncomeAndExpenseWithSameValidJwt() throws Exception {
        String token = createTokenForExistingUser("records@test.com");

        mockMvc.perform(post("/incomes")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "date": "2026-08-18",
                                  "merchantName": "숨고",
                                  "itemName": "디자인 외주",
                                  "amount": 500000,
                                  "withholdingTaxApplied": false,
                                  "category": "SALES"
                                }
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/expenses")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "date": "2026-08-18",
                                  "merchantName": "문구점",
                                  "itemName": "노트",
                                  "amount": 12000,
                                  "category": "SUPPLIES",
                                  "evidenceType": "NON_QUALIFIED",
                                  "qualifiedEvidence": false
                                }
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    void createsExpenseWithKoreanNonQualifiedEvidenceType() throws Exception {
        String token = createTokenForExistingUser("korean-evidence@test.com");

        mockMvc.perform(post("/expenses")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "date": "2026-08-18",
                                  "merchantName": "문구점",
                                  "itemName": "노트",
                                  "amount": 12000,
                                  "category": "SUPPLIES",
                                  "evidenceType": "부적격",
                                  "qualifiedEvidence": false
                                }
                                """))
                .andExpect(status().isCreated());
    }

    private String createTokenForExistingUser(String email) {
        User user = userRepository.save(new User(email, "encoded-password", true, true, true));
        return jwtTokenProvider.createToken(user.getId(), user.getEmail());
    }
}
