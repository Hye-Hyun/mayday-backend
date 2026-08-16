package com.mayday.domain.expense;

import com.mayday.domain.ai.model.ExpenseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    Optional<Expense> findByIdAndDeletedFalse(Long id);

    List<Expense> findByUserIdAndDateBetweenAndDeletedFalseOrderByDateDescIdDesc(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    );

    @Query("""
            select coalesce(sum(e.amount), 0)
            from Expense e
            where e.userId = :userId
              and e.deleted = false
              and e.date between :startDate and :endDate
              and e.category in :categories
            """)
    Long sumAmountByUserIdAndDateBetweenAndCategoryIn(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("categories") Collection<ExpenseCategory> categories
    );
}
