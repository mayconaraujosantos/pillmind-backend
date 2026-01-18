package com.pillmind.presentation.errors;

/**
 * Erro de não autorizado
 */
public class UnauthorizedError extends RuntimeException {
    public UnauthorizedError(String message) {
        super(message);
    }
}
