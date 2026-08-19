package com.mayday.domain.income;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IncomeRepository extends JpaRepository<Income, Long> {

    List<Income> findByUserIdAndDateBetweenAndDeletedFalse(
            Long userId, LocalDate startDate, LocalDate endDate);

    long countByUserIdAndDateBetweenAndDeletedFalse(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    );

    Optional<Income> findByIdAndDeletedFalse(Long id);

    @Query("""
            select coalesce(sum(i.amount), 0)
            from Income i
            where i.userId = :userId
              and i.deleted = false
              and i.date between :startDate and :endDate
            """)
    Long sumAmountByUserIdAndDateBetween(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("""
            select distinct year(i.date)
            from Income i
            where i.userId = :userId
              and i.deleted = false
            order by year(i.date) desc
            """)
    List<Integer> findRecordedYearsByUserId(@Param("userId") Long userId);
}
