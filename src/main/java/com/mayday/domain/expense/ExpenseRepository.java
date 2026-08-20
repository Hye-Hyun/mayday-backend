package com.mayday.domain.expense;

import com.mayday.domain.ai.model.ExpenseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    @Modifying
    @Query("delete from Expense e where e.userId = :userId")
    int deleteAllByUserId(@Param("userId") Long userId);

    Optional<Expense> findByIdAndDeletedFalse(Long id);

    List<Expense> findByUserIdAndDateBetweenAndDeletedFalseOrderByDateDescIdDesc(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    );

    @Query("""
            select distinct year(e.date)
            from Expense e
            where e.userId = :userId
              and e.deleted = false
            order by year(e.date) desc
            """)
    List<Integer> findRecordedYearsByUserId(@Param("userId") Long userId);

    long countByUserIdAndDateBetweenAndDeletedFalse(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    );

    long countByUserIdAndDateBetweenAndDeletedFalseAndQualifiedEvidenceTrue(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    );

    long countByUserIdAndDateBetweenAndDeletedFalseAndQualifiedEvidenceTrueAndCategoryIn(
            Long userId,
            LocalDate startDate,
            LocalDate endDate,
            Collection<ExpenseCategory> categories
    );

    long countByUserIdAndDateBetweenAndDeletedFalseAndAnalysisIdIsNotNull(
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

    @Query("""
            select coalesce(sum(e.amount), 0)
            from Expense e
            where e.userId = :userId
              and e.deleted = false
              and e.analysisId is not null
              and e.date between :startDate and :endDate
              and e.category in :categories
            """)
    Long sumAiAnalyzedAmountByUserIdAndDateBetweenAndCategoryIn(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("categories") Collection<ExpenseCategory> categories
    );
}
