package com.mayday.global.exception;

public class ExpenseAccessDeniedException extends RuntimeException {

    public ExpenseAccessDeniedException(String message) {
        super(message);
    }
}
