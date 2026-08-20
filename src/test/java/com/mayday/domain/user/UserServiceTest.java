package com.mayday.domain.user;

import com.mayday.domain.expense.ExpenseRepository;
import com.mayday.domain.income.IncomeRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final ExpenseRepository expenseRepository = mock(ExpenseRepository.class);
    private final IncomeRepository incomeRepository = mock(IncomeRepository.class);
    private final UserService userService = new UserService(
            userRepository,
            expenseRepository,
            incomeRepository
    );

    @Test
    void withdrawDeletesExpensesBeforeDeletingUser() {
        User user = new User("withdraw@example.com", "encoded-password", true, true, true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.withdraw(1L);

        var order = inOrder(expenseRepository, incomeRepository, userRepository);
        order.verify(expenseRepository).deleteAllByUserId(1L);
        order.verify(incomeRepository).deleteAllByUserId(1L);
        order.verify(userRepository).delete(user);
    }

    @Test
    void withdrawDoesNotDeleteAnythingWhenUserDoesNotExist() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.withdraw(999L));

        verify(expenseRepository, never()).deleteAllByUserId(999L);
        verify(incomeRepository, never()).deleteAllByUserId(999L);
        verify(userRepository, never()).delete(org.mockito.ArgumentMatchers.any());
    }
}
