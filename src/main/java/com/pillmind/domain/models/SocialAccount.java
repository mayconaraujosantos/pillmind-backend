package com.pillmind.domain.models;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade SocialAccount - representa uma conta social vinculada a um usuário
 * Suporta múltiplos provedores OAuth2 por usuário
 */
public record SocialAccount(
        String id,
        String userId,
        String provider,
        String providerUserId,
        String email,
        String name,
        String profileImageUrl,
        String accessToken,
        String refreshToken,
        LocalDateTime tokenExpiry,
        LocalDateTime linkedAt,
        boolean isPrimary) implements Entity {

    // Implementação dos métodos da interface Entity
    @Override
    public LocalDateTime createdAt() {
        return linkedAt; // Como não temos um campo createdAt específico, usamos linkedAt
    }

    @Override
    public LocalDateTime updatedAt() {
        return linkedAt; // Como não temos um campo updatedAt específico, usamos linkedAt
    }

    /**
     * Enum para os provedores suportados
     */
    public enum Provider {
        GOOGLE("GOOGLE"),
        FACEBOOK("FACEBOOK"),
        MICROSOFT("MICROSOFT"),
        APPLE("APPLE");

        private final String value;

        Provider(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static Provider fromString(String value) {
            for (Provider provider : Provider.values()) {
                if (provider.value.equalsIgnoreCase(value)) {
                    return provider;
                }
            }
            throw new IllegalArgumentException("Provedor não suportado: " + value);
        }
    }

    /**
     * Construtor para criação de nova conta social
     */
    public SocialAccount(String userId, String provider, String providerUserId, 
                        String email, String name, String profileImageUrl) {
        this(UUID.randomUUID().toString(), userId, provider, providerUserId, 
             email, name, profileImageUrl, null, null, null, 
             LocalDateTime.now(), false);
    }

    /**
     * Construtor para conta social com tokens OAuth2
     */
    public SocialAccount(String userId, String provider, String providerUserId,
                        String email, String name, String profileImageUrl,
                        String accessToken, String refreshToken, LocalDateTime tokenExpiry) {
        this(UUID.randomUUID().toString(), userId, provider, providerUserId,
             email, name, profileImageUrl, accessToken, refreshToken, tokenExpiry,
             LocalDateTime.now(), false);
    }

    /**
     * Verifica se a conta social tem tokens válidos
     */
    public boolean hasValidTokens() {
        return accessToken != null && 
               (tokenExpiry == null || tokenExpiry.isAfter(LocalDateTime.now()));
    }

    /**
     * Verifica se é do provedor especificado
     */
    public boolean isProvider(Provider provider) {
        return this.provider.equalsIgnoreCase(provider.getValue());
    }

    /**
     * Cria uma cópia definindo como conta primária
     */
    public SocialAccount withPrimary(boolean primary) {
        return new SocialAccount(id, userId, provider, providerUserId, email, name,
                                profileImageUrl, accessToken, refreshToken, tokenExpiry,
                                linkedAt, primary);
    }

    /**
     * Atualiza os tokens OAuth2
     */
    public SocialAccount withTokens(String newAccessToken, String newRefreshToken, 
                                   LocalDateTime newTokenExpiry) {
        return new SocialAccount(id, userId, provider, providerUserId, email, name,
                                profileImageUrl, newAccessToken, newRefreshToken, newTokenExpiry,
                                linkedAt, isPrimary);
    }

    /**
     * Atualiza dados do perfil
     */
    public SocialAccount withUpdatedProfile(String newName, String newEmail, 
                                          String newProfileImageUrl) {
        return new SocialAccount(id, userId, provider, providerUserId, newEmail, newName,
                                newProfileImageUrl, accessToken, refreshToken, tokenExpiry,
                                linkedAt, isPrimary);
    }
}