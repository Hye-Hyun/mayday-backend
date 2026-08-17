package com.mayday.domain.ledger;

import com.mayday.domain.expense.Expense;
import com.mayday.domain.expense.ExpenseRepository;
import com.mayday.domain.ledger.dto.LedgerExportPreviewResponse;
import com.mayday.domain.ledger.dto.LedgerListResponse;
import com.mayday.domain.ledger.dto.LedgerTransactionResponse;
import com.mayday.domain.ledger.dto.LedgerYearsResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;

@Service
public class LedgerService {

    private final ExpenseRepository expenseRepository;
    private final Clock clock;

    public LedgerService(ExpenseRepository expenseRepository, Clock clock) {
        this.expenseRepository = expenseRepository;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public LedgerListResponse getLedger(Long userId, Integer year) {
        int targetYear = requireValidYear(year);
        List<Expense> transactions = findTransactions(userId, targetYear);
        List<LedgerTransactionResponse> responses = transactions.stream()
                .map(LedgerTransactionResponse::from)
                .toList();

        return new LedgerListResponse(responses, responses.size());
    }

    @Transactional(readOnly = true)
    public LedgerYearsResponse getRecordedYears(Long userId) {
        return new LedgerYearsResponse(expenseRepository.findRecordedYearsByUserId(userId));
    }

    @Transactional(readOnly = true)
    public LedgerExportPreviewResponse getExportPreview(Long userId, Integer year) {
        int targetYear = requireValidYear(year);
        List<Expense> transactions = findTransactions(userId, targetYear);
        long totalIncome = transactions.stream()
                .filter(transaction -> transaction.getCategory().isIncome())
                .mapToLong(Expense::getAmount)
                .sum();
        long totalExpense = transactions.stream()
                .filter(transaction -> transaction.getCategory().isExpense())
                .mapToLong(Expense::getAmount)
                .sum();

        return new LedgerExportPreviewResponse(
                new LedgerExportPreviewResponse.Summary(
                        targetYear,
                        transactions.size(),
                        totalIncome,
                        totalExpense
                ),
                transactions.stream()
                        .map(LedgerExportPreviewResponse.Item::from)
                        .toList()
        );
    }

    private List<Expense> findTransactions(Long userId, int year) {
        return expenseRepository.findByUserIdAndDateBetweenAndDeletedFalseOrderByDateDescIdDesc(
                userId,
                LocalDate.of(year, 1, 1),
                LocalDate.of(year, 12, 31)
        );
    }

    private int requireValidYear(Integer year) {
        if (year == null || year < 2000 || year > Year.now(clock).getValue() + 1) {
            throw new IllegalArgumentException("조회 연도가 올바르지 않습니다");
        }
        return year;
    }

}
