package com.pillmind.presentation.errors;

/**
 * Erro interno do servidor
 */
public class ServerError extends RuntimeException {
    public ServerError(String message) {
        super(message);
    }

    public ServerError(String message, Throwable cause) {
        super(message, cause);
    }
}
