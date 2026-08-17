package com.mayday.controller;

import com.mayday.domain.ai.model.EvidenceType;
import com.mayday.domain.ai.model.ExpenseCategory;
import com.mayday.domain.ledger.LedgerService;
import com.mayday.domain.ledger.dto.LedgerExportPreviewResponse;
import com.mayday.domain.ledger.dto.LedgerListResponse;
import com.mayday.domain.ledger.dto.LedgerTransactionResponse;
import com.mayday.domain.ledger.dto.LedgerYearsResponse;
import com.mayday.global.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ledger")
public class LedgerController {

    private final LedgerService ledgerService;

    public LedgerController(LedgerService ledgerService) {
        this.ledgerService = ledgerService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<LedgerListResponse>> getLedger(
            @AuthenticationPrincipal Long userId,
            @RequestParam Integer year
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok("기록 전체 조회 성공", ledgerService.getLedger(userId, year))
        );
    }

    @GetMapping("/years")
    public ResponseEntity<ApiResponse<LedgerYearsResponse>> getRecordedYears(
            @AuthenticationPrincipal Long userId
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok("보유 연도 목록 조회 성공", ledgerService.getRecordedYears(userId))
        );
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<LedgerTransactionResponse>>> searchLedger(
            @AuthenticationPrincipal Long userId,
            @RequestParam Integer year,
            @RequestParam String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) ExpenseCategory category,
            @RequestParam(required = false) EvidenceType evidenceType,
            @RequestParam(required = false) Boolean qualifiedEvidence,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok(
                        "기록 검색 성공",
                        ledgerService.search(
                                userId,
                                year,
                                keyword,
                                type,
                                category,
                                evidenceType,
                                qualifiedEvidence,
                                page,
                                size
                        )
                )
        );
    }

    @GetMapping("/export")
    public ResponseEntity<ApiResponse<LedgerExportPreviewResponse>> getExportPreview(
            @AuthenticationPrincipal Long userId,
            @RequestParam Integer year
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok("내보내기 미리보기 데이터 조회 성공", ledgerService.getExportPreview(userId, year))
        );
    }
}
