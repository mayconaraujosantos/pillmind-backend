package com.pillmind.presentation.errors;

/**
 * Erro de recurso não encontrado
 */
public class NotFoundError extends RuntimeException {
    public NotFoundError(String message) {
        super(message);
    }
}
