package com.mayday.domain.ledger;

import com.mayday.domain.expense.Expense;
import com.mayday.domain.expense.ExpenseRepository;
import com.mayday.domain.income.Income;
import com.mayday.domain.income.IncomeRepository;
import com.mayday.domain.ledger.dto.LedgerExportPreviewResponse;
import com.mayday.domain.ledger.dto.LedgerListResponse;
import com.mayday.domain.ledger.dto.LedgerTransactionResponse;
import com.mayday.domain.ledger.dto.LedgerYearsResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.Year;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
public class LedgerService {

    private final ExpenseRepository expenseRepository;
    private final IncomeRepository incomeRepository;
    private final Clock clock;

    public LedgerService(ExpenseRepository expenseRepository, IncomeRepository incomeRepository, Clock clock) {
        this.expenseRepository = expenseRepository;
        this.incomeRepository = incomeRepository;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public LedgerListResponse getLedger(Long userId, Integer year) {
        int targetYear = requireValidYear(year);
        List<Expense> expenses = findExpenses(userId, targetYear);
        List<Income> incomes = findIncomes(userId, targetYear);
        List<LedgerTransactionResponse> responses = Stream.concat(
                        expenses.stream().map(LedgerTransactionResponse::from),
                        incomes.stream().map(LedgerTransactionResponse::from)
                )
                .sorted(Comparator.comparing(LedgerTransactionResponse::getDate).reversed())
                .toList();

        return new LedgerListResponse(responses, responses.size());
    }

    @Transactional(readOnly = true)
    public LedgerYearsResponse getRecordedYears(Long userId) {
        List<Integer> years = Stream.concat(
                        expenseRepository.findRecordedYearsByUserId(userId).stream(),
                        incomeRepository.findRecordedYearsByUserId(userId).stream()
                )
                .distinct()
                .sorted(Comparator.reverseOrder())
                .toList();
        return new LedgerYearsResponse(years);
    }

    @Transactional(readOnly = true)
    public LedgerExportPreviewResponse getExportPreview(Long userId, Integer year) {
        int targetYear = requireValidYear(year);
        List<Expense> expenses = findExpenses(userId, targetYear);
        List<Income> incomes = findIncomes(userId, targetYear);
        long totalIncome = expenses.stream()
                .filter(transaction -> transaction.getCategory().isIncome())
                .mapToLong(Expense::getAmount)
                .sum()
                + incomes.stream()
                .mapToLong(Income::getAmount)
                .sum();
        long totalExpense = expenses.stream()
                .filter(transaction -> transaction.getCategory().isExpense())
                .mapToLong(Expense::getAmount)
                .sum();
        List<LedgerExportPreviewResponse.Item> items = Stream.concat(
                        expenses.stream().map(LedgerExportPreviewResponse.Item::from),
                        incomes.stream().map(LedgerExportPreviewResponse.Item::from)
                )
                .sorted(Comparator.comparing(LedgerExportPreviewResponse.Item::getDate).reversed())
                .toList();

        return new LedgerExportPreviewResponse(
                new LedgerExportPreviewResponse.Summary(
                        targetYear,
                        items.size(),
                        totalIncome,
                        totalExpense
                ),
                items
        );
    }

    private List<Expense> findExpenses(Long userId, int year) {
        return expenseRepository.findByUserIdAndDateBetweenAndDeletedFalseOrderByDateDescIdDesc(
                userId,
                LocalDate.of(year, 1, 1),
                LocalDate.of(year, 12, 31)
        );
    }

    private List<Income> findIncomes(Long userId, int year) {
        return incomeRepository.findByUserIdAndDateBetweenAndDeletedFalse(
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
