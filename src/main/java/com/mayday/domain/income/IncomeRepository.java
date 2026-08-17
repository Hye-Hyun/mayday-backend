package com.mayday.domain.income;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IncomeRepository extends JpaRepository<Income, Long> {

    List<Income> findByUserIdAndDateBetweenAndDeletedFalse(
            Long userId, LocalDate startDate, LocalDate endDate);

    Optional<Income> findByIdAndDeletedFalse(Long id);
}