package com.mayday.global.exception;

import org.springframework.http.HttpStatus;

public class OcrProcessingException extends RuntimeException {
    private final HttpStatus status;

    public OcrProcessingException(String message) {
        super(message);
        this.status = HttpStatus.BAD_GATEWAY;
    }

    public OcrProcessingException(String message, Throwable cause) {
        super(message, cause);
        this.status = HttpStatus.BAD_GATEWAY;
    }

    public OcrProcessingException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public OcrProcessingException(String message, Throwable cause, HttpStatus status) {
        super(message, cause);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
