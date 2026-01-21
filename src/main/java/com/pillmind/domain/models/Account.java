package com.pillmind.domain.models;

import java.time.LocalDateTime;

/**
 * Entidade Account - representa uma conta de usuário
 */
public record Account(
        String id,
        String name,
        String email,
        String password,
        boolean googleAccount,
        String googleId,
        String pictureUrl,
        LocalDateTime lastLoginAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) implements Entity {

    /**
     * Construtor para criação de nova conta (timestamps automáticos)
     */
    public Account(String id, String name, String email, String password, boolean googleAccount) {
        this(id, name, email, password, googleAccount, null, null, null, LocalDateTime.now(), LocalDateTime.now());
    }

    /**
     * Construtor de conveniência para contas OAuth2 com dados adicionais
     */
    public Account(String id, String name, String email, String password, boolean googleAccount, String googleId,
            String pictureUrl, LocalDateTime lastLoginAt) {
        this(id, name, email, password, googleAccount, googleId, pictureUrl, lastLoginAt, LocalDateTime.now(),
                LocalDateTime.now());
    }

    /**
     * Verifica se a conta usa autenticação do Google
     */
    public boolean isGoogleAccount() {
        return googleAccount;
    }

    /**
     * Verifica se a conta tem senha definida
     */
    public boolean hasPassword() {
        return password != null && !password.isBlank();
    }

    /**
     * Cria uma cópia atualizada da conta
     */
    public Account withUpdatedTimestamp() {
        return new Account(id, name, email, password, googleAccount, googleId, pictureUrl, lastLoginAt, createdAt,
                LocalDateTime.now());
    }

    /**
     * Atualiza perfil (nome/foto) preservando restante
     */
    public Account withUpdatedProfile(String newName, String newPictureUrl) {
        return new Account(id, newName, email, password, googleAccount, googleId, newPictureUrl, lastLoginAt,
                createdAt, LocalDateTime.now());
    }

    /**
     * Atualiza lastLoginAt
     */
    public Account withLastLoginAt(LocalDateTime loginAt) {
        return new Account(id, name, email, password, googleAccount, googleId, pictureUrl, loginAt, createdAt,
                LocalDateTime.now());
    }

    /**
     * Atualiza dados de conta Google (googleId, foto, nome)
     */
    public Account withGoogleData(String newName, String newGoogleId, String newPictureUrl, LocalDateTime loginAt) {
        return new Account(id, newName, email, password, googleAccount, newGoogleId, newPictureUrl, loginAt, createdAt,
                LocalDateTime.now());
    }
}
