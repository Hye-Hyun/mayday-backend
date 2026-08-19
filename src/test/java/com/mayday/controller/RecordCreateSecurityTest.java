package com.mayday.controller;

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

    @Test
    void createsIncomeAndExpenseWithSameValidJwt() throws Exception {
        String token = jwtTokenProvider.createToken(1L, "test@test.com");

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
}
