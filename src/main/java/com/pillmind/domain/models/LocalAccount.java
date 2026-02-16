package com.pillmind.domain.models;

import java.time.LocalDateTime;

/**
 * Entidade LocalAccount - representa uma conta de autenticação local (email/senha)
 */
public record LocalAccount(
        String id,
        String userId,
        String email,
        String passwordHash,
        LocalDateTime lastLoginAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) implements Entity {

    /**
     * Construtor para criação de nova conta local (timestamps automáticos)
     */
    public LocalAccount(String id, String userId, String email, String passwordHash) {
        this(id, userId, email, passwordHash, null, LocalDateTime.now(), LocalDateTime.now());
    }

    /**
     * Atualiza timestamp de último login
     */
    public LocalAccount withLastLoginAt(LocalDateTime loginAt) {
        return new LocalAccount(id, userId, email, passwordHash, loginAt, createdAt, LocalDateTime.now());
    }

    /**
     * Atualiza senha
     */
    public LocalAccount withNewPassword(String newPasswordHash) {
        return new LocalAccount(id, userId, email, newPasswordHash, lastLoginAt, createdAt, LocalDateTime.now());
    }

    /**
     * Atualiza email
     */
    public LocalAccount withNewEmail(String newEmail) {
        return new LocalAccount(id, userId, newEmail, passwordHash, lastLoginAt, createdAt, LocalDateTime.now());
    }

    /**
     * Atualiza apenas timestamp
     */
    public LocalAccount withUpdatedTimestamp() {
        return new LocalAccount(id, userId, email, passwordHash, lastLoginAt, createdAt, LocalDateTime.now());
    }

    /**
     * Verifica se a conta tem senha definida
     */
    public boolean hasPassword() {
        return passwordHash != null && !passwordHash.isBlank();
    }
}