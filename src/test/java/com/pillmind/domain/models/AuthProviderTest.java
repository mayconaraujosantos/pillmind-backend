package com.pillmind.domain.models;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para o enum AuthProvider
 */
@DisplayName("AuthProvider Tests")
public class AuthProviderTest {

    @Test
    @DisplayName("Deve converter string para AuthProvider enum")
    void shouldConvertStringToAuthProviderEnum() {
        assertEquals(AuthProvider.LOCAL, AuthProvider.fromString("LOCAL"));
        assertEquals(AuthProvider.LOCAL, AuthProvider.fromString("local"));
        assertEquals(AuthProvider.GOOGLE, AuthProvider.fromString("GOOGLE"));
        assertEquals(AuthProvider.GOOGLE, AuthProvider.fromString("google"));
        assertEquals(AuthProvider.FACEBOOK, AuthProvider.fromString("FACEBOOK"));
        assertEquals(AuthProvider.MICROSOFT, AuthProvider.fromString("MICROSOFT"));
        assertEquals(AuthProvider.APPLE, AuthProvider.fromString("APPLE"));
    }

    @Test
    @DisplayName("Deve retornar LOCAL como fallback para null")
    void shouldReturnLocalForNullValue() {
        assertEquals(AuthProvider.LOCAL, AuthProvider.fromString(null));
    }

    @Test
    @DisplayName("Deve lançar exceção para valor inválido")
    void shouldThrowExceptionForInvalidValue() {
        assertThrows(IllegalArgumentException.class, () -> 
                AuthProvider.fromString("INVALID_PROVIDER"));
    }

    @Test
    @DisplayName("Deve identificar provedores OAuth2")
    void shouldIdentifyOAuth2Providers() {
        assertTrue(AuthProvider.GOOGLE.isOAuth2());
        assertTrue(AuthProvider.FACEBOOK.isOAuth2());
        assertTrue(AuthProvider.MICROSOFT.isOAuth2());
        assertTrue(AuthProvider.APPLE.isOAuth2());
        assertFalse(AuthProvider.LOCAL.isOAuth2());
    }

    @Test
    @DisplayName("Deve identificar provedor local")
    void shouldIdentifyLocalProvider() {
        assertTrue(AuthProvider.LOCAL.isLocal());
        assertFalse(AuthProvider.GOOGLE.isLocal());
        assertFalse(AuthProvider.FACEBOOK.isLocal());
        assertFalse(AuthProvider.MICROSOFT.isLocal());
        assertFalse(AuthProvider.APPLE.isLocal());
    }

    @Test
    @DisplayName("Deve retornar valores corretos")
    void shouldReturnCorrectValues() {
        assertEquals("LOCAL", AuthProvider.LOCAL.getValue());
        assertEquals("GOOGLE", AuthProvider.GOOGLE.getValue());
        assertEquals("FACEBOOK", AuthProvider.FACEBOOK.getValue());
        assertEquals("MICROSOFT", AuthProvider.MICROSOFT.getValue());
        assertEquals("APPLE", AuthProvider.APPLE.getValue());
    }
}