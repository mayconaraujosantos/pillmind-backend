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
        AuthProvider authProvider,
        boolean emailVerified,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) implements Entity {

    /**
     * Construtor para criação de nova conta (timestamps automáticos)
     */
    public Account(String id, String name, String email, String password, boolean googleAccount) {
        this(id, name, email, password, googleAccount, null, null, null, 
             googleAccount ? AuthProvider.GOOGLE : AuthProvider.LOCAL, googleAccount,
             LocalDateTime.now(), LocalDateTime.now());
    }

    /**
     * Construtor de conveniência para contas OAuth2 com dados adicionais
     */
    public Account(String id, String name, String email, String password, boolean googleAccount, String googleId,
            String pictureUrl, LocalDateTime lastLoginAt) {
        this(id, name, email, password, googleAccount, googleId, pictureUrl, lastLoginAt,
             googleAccount ? AuthProvider.GOOGLE : AuthProvider.LOCAL, googleAccount,
             LocalDateTime.now(), LocalDateTime.now());
    }

    /**
     * Construtor completo para migration/repository use
     */
    public Account(String id, String name, String email, String password, boolean googleAccount, 
                  String googleId, String pictureUrl, LocalDateTime lastLoginAt,
                  String authProvider, boolean emailVerified,
                  LocalDateTime createdAt, LocalDateTime updatedAt) {
        this(id, name, email, password, googleAccount, googleId, pictureUrl, lastLoginAt,
             AuthProvider.fromString(authProvider), emailVerified, createdAt, updatedAt);
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
        return new Account(id, name, email, password, googleAccount, googleId, pictureUrl, lastLoginAt,
                authProvider, emailVerified, createdAt, LocalDateTime.now());
    }

    /**
     * Atualiza perfil (nome/foto) preservando restante
     */
    public Account withUpdatedProfile(String newName, String newPictureUrl) {
        return new Account(id, newName, email, password, googleAccount, googleId, newPictureUrl, lastLoginAt,
                authProvider, emailVerified, createdAt, LocalDateTime.now());
    }

    /**
     * Atualiza lastLoginAt
     */
    public Account withLastLoginAt(LocalDateTime loginAt) {
        return new Account(id, name, email, password, googleAccount, googleId, pictureUrl, loginAt,
                authProvider, emailVerified, createdAt, LocalDateTime.now());
    }

    /**
     * Atualiza dados de conta Google (googleId, foto, nome)
     */
    public Account withGoogleData(String newName, String newGoogleId, String newPictureUrl, LocalDateTime loginAt) {
        return new Account(id, newName, email, password, googleAccount, newGoogleId, newPictureUrl, loginAt,
                authProvider, emailVerified, createdAt, LocalDateTime.now());
    }

    /**
     * Marca email como verificado
     */
    public Account withEmailVerified(boolean verified) {
        return new Account(id, name, email, password, googleAccount, googleId, pictureUrl, lastLoginAt,
                authProvider, verified, createdAt, LocalDateTime.now());
    }

    /**
     * Atualiza o provedor de autenticação
     */
    public Account withAuthProvider(AuthProvider provider) {
        return new Account(id, name, email, password, googleAccount, googleId, pictureUrl, lastLoginAt,
                provider, emailVerified, createdAt, LocalDateTime.now());
    }

    /**
     * Verifica se a conta usa um provedor OAuth2
     */
    public boolean isOAuth2Account() {
        return authProvider.isOAuth2();
    }

    /**
     * Verifica se a conta usa autenticação local
     */
    public boolean isLocalAccount() {
        return authProvider.isLocal();
    }
}
