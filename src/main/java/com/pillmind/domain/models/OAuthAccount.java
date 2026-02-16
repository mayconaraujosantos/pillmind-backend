package com.pillmind.domain.models;

import java.time.LocalDateTime;

/**
 * Entidade OAuthAccount - representa uma conta de autenticação OAuth2 (Google, Facebook, etc.)
 */
public record OAuthAccount(
        String id,
        String userId,
        AuthProvider provider,
        String providerUserId,
        String email,
        String providerName,
        String profileImageUrl,
        String accessToken,
        String refreshToken,
        LocalDateTime tokenExpiry,
        LocalDateTime lastLoginAt,
        LocalDateTime linkedAt,
        boolean isPrimary,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) implements Entity {

    /**
     * Construtor para criação de nova conta OAuth2 (timestamps automáticos)
     */
    public OAuthAccount(String id, String userId, AuthProvider provider, String providerUserId, 
                       String email, String providerName, String profileImageUrl) {
        this(id, userId, provider, providerUserId, email, providerName, profileImageUrl,
             null, null, null, null, LocalDateTime.now(), true, 
             LocalDateTime.now(), LocalDateTime.now());
    }

    /**
     * Construtor completo com tokens
     */
    public OAuthAccount(String id, String userId, AuthProvider provider, String providerUserId,
                       String email, String providerName, String profileImageUrl,
                       String accessToken, String refreshToken, LocalDateTime tokenExpiry) {
        this(id, userId, provider, providerUserId, email, providerName, profileImageUrl,
             accessToken, refreshToken, tokenExpiry, null, LocalDateTime.now(), true,
             LocalDateTime.now(), LocalDateTime.now());
    }

    /**
     * Atualiza dados do provedor (nome, foto, email)
     */
    public OAuthAccount withUpdatedProviderData(String newProviderName, String newEmail, String newProfileImageUrl) {
        return new OAuthAccount(id, userId, provider, providerUserId, newEmail, newProviderName, 
                               newProfileImageUrl, accessToken, refreshToken, tokenExpiry, 
                               lastLoginAt, linkedAt, isPrimary, createdAt, LocalDateTime.now());
    }

    /**
     * Atualiza tokens
     */
    public OAuthAccount withUpdatedTokens(String newAccessToken, String newRefreshToken, LocalDateTime newTokenExpiry) {
        return new OAuthAccount(id, userId, provider, providerUserId, email, providerName, 
                               profileImageUrl, newAccessToken, newRefreshToken, newTokenExpiry, 
                               lastLoginAt, linkedAt, isPrimary, createdAt, LocalDateTime.now());
    }

    /**
     * Atualiza timestamp de último login
     */
    public OAuthAccount withLastLoginAt(LocalDateTime loginAt) {
        return new OAuthAccount(id, userId, provider, providerUserId, email, providerName, 
                               profileImageUrl, accessToken, refreshToken, tokenExpiry, 
                               loginAt, linkedAt, isPrimary, createdAt, LocalDateTime.now());
    }

    /**
     * Define como conta primária
     */
    public OAuthAccount withPrimary(boolean primary) {
        return new OAuthAccount(id, userId, provider, providerUserId, email, providerName, 
                               profileImageUrl, accessToken, refreshToken, tokenExpiry, 
                               lastLoginAt, linkedAt, primary, createdAt, LocalDateTime.now());
    }

    /**
     * Atualiza apenas timestamp
     */
    public OAuthAccount withUpdatedTimestamp() {
        return new OAuthAccount(id, userId, provider, providerUserId, email, providerName, 
                               profileImageUrl, accessToken, refreshToken, tokenExpiry, 
                               lastLoginAt, linkedAt, isPrimary, createdAt, LocalDateTime.now());
    }

    /**
     * Verifica se os tokens estão expirados
     */
    public boolean isTokenExpired() {
        return tokenExpiry != null && LocalDateTime.now().isAfter(tokenExpiry);
    }

    /**
     * Verifica se tem tokens válidos
     */
    public boolean hasValidTokens() {
        return accessToken != null && !accessToken.isBlank() && !isTokenExpired();
    }
}