package com.pillmind.domain.models;

/**
 * Enum para os provedores de autenticação suportados
 */
public enum AuthProvider {
    LOCAL("LOCAL"),
    GOOGLE("GOOGLE"),
    FACEBOOK("FACEBOOK"),
    MICROSOFT("MICROSOFT"),
    APPLE("APPLE");

    private final String value;

    AuthProvider(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static AuthProvider fromString(String value) {
        if (value == null) {
            return LOCAL; // Default fallback
        }
        
        for (AuthProvider provider : AuthProvider.values()) {
            if (provider.value.equalsIgnoreCase(value)) {
                return provider;
            }
        }
        throw new IllegalArgumentException("Provedor de autenticação não suportado: " + value);
    }

    /**
     * Verifica se é um provedor OAuth2 (não local)
     */
    public boolean isOAuth2() {
        return this != LOCAL;
    }

    /**
     * Verifica se é autenticação local (email/senha)
     */
    public boolean isLocal() {
        return this == LOCAL;
    }
}