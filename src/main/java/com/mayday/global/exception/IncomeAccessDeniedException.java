package com.mayday.global.exception;

public class IncomeAccessDeniedException extends RuntimeException {
    public IncomeAccessDeniedException(String message) {
        super(message);
    }
}