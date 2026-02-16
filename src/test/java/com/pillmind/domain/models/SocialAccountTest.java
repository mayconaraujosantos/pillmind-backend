package com.pillmind.domain.models;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para a entidade SocialAccount
 */
@DisplayName("SocialAccount Tests")
public class SocialAccountTest {

    @Test
    @DisplayName("Deve criar conta social com dados válidos")
    void shouldCreateSocialAccountWithValidData() {
        var socialAccount = new SocialAccount(
                "social-account-123",
                "user-456",
                "GOOGLE",
                "google-123",
                "user@example.com",
                "John Doe",
                "http://example.com/photo.jpg",
                "access-token",
                "refresh-token",
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now(),
                true
        );

        assertEquals("social-account-123", socialAccount.id());
        assertEquals("user-456", socialAccount.userId());
        assertEquals("GOOGLE", socialAccount.provider());
        assertEquals("google-123", socialAccount.providerUserId());
        assertEquals("user@example.com", socialAccount.email());
        assertEquals("John Doe", socialAccount.name());
        assertEquals("http://example.com/photo.jpg", socialAccount.profileImageUrl());
        assertEquals("access-token", socialAccount.accessToken());
        assertEquals("refresh-token", socialAccount.refreshToken());
        assertTrue(socialAccount.isPrimary());
    }

    @Test
    @DisplayName("Deve criar conta social usando construtor simples")
    void shouldCreateSocialAccountWithSimpleConstructor() {
        var socialAccount = new SocialAccount(
                "user-456",
                "GOOGLE",
                "google-123",
                "user@example.com",
                "John Doe",
                "http://example.com/photo.jpg"
        );

        assertEquals("user-456", socialAccount.userId());
        assertEquals("GOOGLE", socialAccount.provider());
        assertEquals("google-123", socialAccount.providerUserId());
        assertEquals("user@example.com", socialAccount.email());
        assertEquals("John Doe", socialAccount.name());
        assertEquals("http://example.com/photo.jpg", socialAccount.profileImageUrl());
        assertNotNull(socialAccount.id());
        assertNotNull(socialAccount.linkedAt());
        assertFalse(socialAccount.isPrimary());
    }

    @Test
    @DisplayName("Deve verificar se tem tokens válidos")
    void shouldCheckValidTokens() {
        // Token válido (futuro)
        var validAccount = new SocialAccount(
                "user-456",
                "GOOGLE",
                "google-123",
                "user@example.com",
                "John Doe",
                "http://example.com/photo.jpg",
                "access-token",
                "refresh-token",
                LocalDateTime.now().plusHours(1)
        );

        assertTrue(validAccount.hasValidTokens());

        // Token expirado
        var expiredAccount = new SocialAccount(
                "user-456",
                "GOOGLE",
                "google-123",
                "user@example.com",
                "John Doe",
                "http://example.com/photo.jpg",
                "access-token",
                "refresh-token",
                LocalDateTime.now().minusHours(1)
        );

        assertFalse(expiredAccount.hasValidTokens());

        // Sem token
        var noTokenAccount = new SocialAccount(
                "user-456",
                "GOOGLE",
                "google-123",
                "user@example.com",
                "John Doe",
                "http://example.com/photo.jpg"
        );

        assertFalse(noTokenAccount.hasValidTokens());
    }

    @Test
    @DisplayName("Deve verificar provedor")
    void shouldCheckProvider() {
        var googleAccount = new SocialAccount(
                "user-456",
                "GOOGLE",
                "google-123",
                "user@example.com",
                "John Doe",
                "http://example.com/photo.jpg"
        );

        assertTrue(googleAccount.isProvider(SocialAccount.Provider.GOOGLE));
        assertFalse(googleAccount.isProvider(SocialAccount.Provider.FACEBOOK));
    }

    @Test
    @DisplayName("Deve atualizar status primário")
    void shouldUpdatePrimaryStatus() {
        var socialAccount = new SocialAccount(
                "user-456",
                "GOOGLE",
                "google-123",
                "user@example.com",
                "John Doe",
                "http://example.com/photo.jpg"
        );

        assertFalse(socialAccount.isPrimary());

        var primaryAccount = socialAccount.withPrimary(true);

        assertTrue(primaryAccount.isPrimary());
        assertEquals(socialAccount.id(), primaryAccount.id());
        assertEquals(socialAccount.userId(), primaryAccount.userId());
    }

    @Test
    @DisplayName("Deve atualizar tokens")
    void shouldUpdateTokens() {
        var socialAccount = new SocialAccount(
                "user-456",
                "GOOGLE",
                "google-123",
                "user@example.com",
                "John Doe",
                "http://example.com/photo.jpg"
        );

        var newExpiry = LocalDateTime.now().plusDays(1);
        var updatedAccount = socialAccount.withTokens("new-access", "new-refresh", newExpiry);

        assertEquals("new-access", updatedAccount.accessToken());
        assertEquals("new-refresh", updatedAccount.refreshToken());
        assertEquals(newExpiry, updatedAccount.tokenExpiry());
        assertEquals(socialAccount.id(), updatedAccount.id());
        assertEquals(socialAccount.userId(), updatedAccount.userId());
    }

    @Test
    @DisplayName("Deve atualizar perfil")
    void shouldUpdateProfile() {
        var socialAccount = new SocialAccount(
                "user-456",
                "GOOGLE",
                "google-123",
                "old@example.com",
                "Old Name",
                "http://old.com/photo.jpg"
        );

        var updatedAccount = socialAccount.withUpdatedProfile(
                "New Name", 
                "new@example.com", 
                "http://new.com/photo.jpg"
        );

        assertEquals("New Name", updatedAccount.name());
        assertEquals("new@example.com", updatedAccount.email());
        assertEquals("http://new.com/photo.jpg", updatedAccount.profileImageUrl());
        assertEquals(socialAccount.id(), updatedAccount.id());
        assertEquals(socialAccount.userId(), updatedAccount.userId());
    }

    @Test
    @DisplayName("Deve implementar interface Entity")
    void shouldImplementEntityInterface() {
        var socialAccount = new SocialAccount(
                "user-456",
                "GOOGLE",
                "google-123",
                "user@example.com",
                "John Doe",
                "http://example.com/photo.jpg"
        );

        assertTrue(socialAccount instanceof Entity);
        assertNotNull(socialAccount.id());
    }

    @Test
    @DisplayName("Deve converter string para Provider enum")
    void shouldConvertStringToProviderEnum() {
        assertEquals(SocialAccount.Provider.GOOGLE, SocialAccount.Provider.fromString("GOOGLE"));
        assertEquals(SocialAccount.Provider.GOOGLE, SocialAccount.Provider.fromString("google"));
        assertEquals(SocialAccount.Provider.FACEBOOK, SocialAccount.Provider.fromString("FACEBOOK"));

        assertThrows(IllegalArgumentException.class, () -> 
                SocialAccount.Provider.fromString("INVALID_PROVIDER"));
    }
}