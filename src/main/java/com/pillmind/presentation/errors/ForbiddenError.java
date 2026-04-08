package com.pillmind.presentation.errors;

/**
 * Erro de acesso negado
 */
public class ForbiddenError extends RuntimeException {
    public ForbiddenError(String message) {
        super(message);
    }
}
