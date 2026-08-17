package com.mayday.domain.income;

import com.mayday.domain.ai.model.ExpenseCategory;
import com.mayday.domain.income.dto.IncomeProgressResponse;
import com.mayday.domain.income.model.IncomeStatus;
import com.mayday.domain.user.JobCategory;
import com.mayday.domain.user.User;
import com.mayday.domain.user.UserRepository;
import com.mayday.global.exception.IndustryCategoryNotSetException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Year;

import com.mayday.domain.income.dto.IncomeCreateRequest;
import com.mayday.domain.income.dto.IncomeCreateResponse;
import com.mayday.domain.income.dto.IncomeDetailResponse;
import com.mayday.domain.income.dto.IncomeListItemResponse;
import com.mayday.domain.income.dto.IncomeListResponse;
import com.mayday.domain.income.dto.IncomeUpdateRequest;
import com.mayday.domain.income.dto.IncomeUpdateResponse;
import com.mayday.global.exception.IncomeAccessDeniedException;
import com.mayday.global.exception.IncomeNotFoundException;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class IncomeService {

    private static final double NEAR_THRESHOLD_RATIO = 0.8;

    private final IncomeRepository incomeRepository;
    private final UserRepository userRepository;

    public IncomeService(IncomeRepository incomeRepository, UserRepository userRepository) {
        this.incomeRepository = incomeRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public IncomeProgressResponse getProgress(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        JobCategory jobCategory = user.getJobCategory();
        if (jobCategory == null) {
            throw new IndustryCategoryNotSetException("업종군이 설정되지 않았습니다");
        }

        int currentYear = Year.now().getValue();
        LocalDate startDate = LocalDate.of(currentYear, 1, 1);
        LocalDate endDate = LocalDate.of(currentYear, 12, 31);

        long cumulativeIncome = incomeRepository
                .findByUserIdAndDateBetweenAndDeletedFalse(userId, startDate, endDate)
                .stream()
                .mapToLong(Income::getAmount)
                .sum();

        long standardAmount = jobCategory.getStandardAmount();
        long remainingAmount = Math.max(standardAmount - cumulativeIncome, 0);
        double rate = (double) cumulativeIncome / standardAmount;

        IncomeStatus status = resolveStatus(rate);

        return new IncomeProgressResponse(
                cumulativeIncome,
                standardAmount,
                remainingAmount,
                status,
                buildMessage(status, remainingAmount),
                status != IncomeStatus.SAFE
        );
    }

    private IncomeStatus resolveStatus(double rate) {
        if (rate > 1.0) return IncomeStatus.EXCEEDED;
        if (rate == 1.0) return IncomeStatus.REACHED;
        if (rate >= NEAR_THRESHOLD_RATIO) return IncomeStatus.NEAR;
        return IncomeStatus.SAFE;
    }

    private String buildMessage(IncomeStatus status, long remainingAmount) {
        return switch (status) {
            case SAFE -> String.format("아직 여유가 있어요. 기준 금액까지 %,d만 원 남았어요.", remainingAmount / 10_000);
            case NEAR -> "기준 금액에 가까워지고 있어요. 지금부터 경비를 꼼꼼히 기록해보세요.";
            case REACHED -> "기준 금액에 도달했어요. 지금부터는 경비 기록이 특히 중요해요.";
            case EXCEEDED -> "기준 금액을 초과했어요. 누락된 경비가 없는지 확인해보세요.";
        };
    }

    private static final String INCOME_ID_PREFIX = "inc_";
    private static final ZoneOffset KOREA_OFFSET = ZoneOffset.ofHours(9);

    @Transactional
    public IncomeCreateResponse create(Long userId, IncomeCreateRequest request) {
        validateIncomeCategory(request.getCategory());
        validateReceivedAmount(request.resolveReceivedAmount(), request.getAmount());
        Income income = Income.create(userId, request);
        incomeRepository.save(income);
        return new IncomeCreateResponse(formatIncomeId(income.getId()));
    }

    @Transactional(readOnly = true)
    public IncomeListResponse getListByYear(Long userId, Integer year) {
        int targetYear = year == null ? Year.now().getValue() : year;
        LocalDate startDate = LocalDate.of(targetYear, 1, 1);
        LocalDate endDate = LocalDate.of(targetYear, 12, 31);

        List<IncomeListItemResponse> incomes = incomeRepository
                .findByUserIdAndDateBetweenAndDeletedFalse(userId, startDate, endDate)
                .stream()
                .map(income -> IncomeListItemResponse.from(income, formatIncomeId(income.getId())))
                .toList();

        return new IncomeListResponse(targetYear, incomes.size(), incomes);
    }

    @Transactional(readOnly = true)
    public IncomeDetailResponse getDetail(Long userId, String incomeId) {
        Income income = getIncome(incomeId);
        validateOwner(userId, income);
        return IncomeDetailResponse.from(income, formatIncomeId(income.getId()));
    }

    @Transactional
    public IncomeUpdateResponse update(Long userId, String incomeId, IncomeUpdateRequest request) {
        Income income = getIncome(incomeId);
        validateOwner(userId, income);
        validateIncomeCategory(request.getCategory());
        validateReceivedAmount(request.resolveReceivedAmount(), request.getAmount());
        income.update(request);

        return new IncomeUpdateResponse(
                formatIncomeId(income.getId()),
                OffsetDateTime.of(income.getUpdatedAt(), KOREA_OFFSET)
        );
    }

    @Transactional
    public void delete(Long userId, String incomeId) {
        Income income = getIncome(incomeId);
        validateOwner(userId, income);
        income.delete();
    }

    private Income getIncome(String incomeId) {
        Long id = parseIncomeId(incomeId);
        return incomeRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new IncomeNotFoundException("수입 기록을 찾을 수 없습니다"));
    }

    private void validateOwner(Long userId, Income income) {
        if (!income.getUserId().equals(userId)) {
            throw new IncomeAccessDeniedException("수정 권한이 없습니다");
        }
    }

    private void validateIncomeCategory(ExpenseCategory category) {
        if (category == null || !category.isIncome()) {
            throw new IllegalArgumentException("수입 계정과목이 올바르지 않습니다");
        }
    }

    private void validateReceivedAmount(Long receivedAmount, Long grossAmount) {
        if (receivedAmount == null || receivedAmount <= 0 || receivedAmount > grossAmount) {
            throw new IllegalArgumentException("실수령 금액이 올바르지 않습니다");
        }
    }

    private Long parseIncomeId(String incomeId) {
        try {
            String id = incomeId.startsWith(INCOME_ID_PREFIX)
                    ? incomeId.substring(INCOME_ID_PREFIX.length())
                    : incomeId;
            return Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new IncomeNotFoundException("수입 기록을 찾을 수 없습니다");
        }
    }

    private String formatIncomeId(Long id) {
        return INCOME_ID_PREFIX + String.format("%03d", id);
    }
}
