package com.pillmind.domain.errors;

/**
 * Erro de conflito de estado (ex.: recurso já existe).
 */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }

    public ConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
