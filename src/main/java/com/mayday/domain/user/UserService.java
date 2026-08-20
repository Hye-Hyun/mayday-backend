package com.mayday.domain.user;

import com.mayday.domain.user.dto.OnboardingRequest;
import com.mayday.domain.expense.ExpenseRepository;
import com.mayday.domain.income.IncomeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;
    private final IncomeRepository incomeRepository;

    public UserService(
            UserRepository userRepository,
            ExpenseRepository expenseRepository,
            IncomeRepository incomeRepository
    ) {
        this.userRepository = userRepository;
        this.expenseRepository = expenseRepository;
        this.incomeRepository = incomeRepository;
    }

    @Transactional
    public void completeOnboarding(Long userId, OnboardingRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        user.completeOnboarding(request.getJobCategory(), request.getInitialIncome());
    }

    @Transactional
    public void withdraw(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        expenseRepository.deleteAllByUserId(userId);
        incomeRepository.deleteAllByUserId(userId);
        userRepository.delete(user);
    }
}
