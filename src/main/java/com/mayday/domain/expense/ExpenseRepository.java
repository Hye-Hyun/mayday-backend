package com.mayday.domain.expense;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    Optional<Expense> findByIdAndDeletedFalse(Long id);

    List<Expense> findByUserIdAndDateBetweenAndDeletedFalseOrderByDateDescIdDesc(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    );
}
