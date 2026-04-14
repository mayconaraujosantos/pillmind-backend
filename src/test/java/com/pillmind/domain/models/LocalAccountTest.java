package com.pillmind.domain.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("LocalAccount")
class LocalAccountTest {

    @Test
    @DisplayName("Should create local account with minimal constructor")
    void shouldCreateWithMinimalConstructor() {
        var account = new LocalAccount("la-id", "user-id", "user@example.com", "hashed-pw");

        assertEquals("la-id", account.id());
        assertEquals("user-id", account.userId());
        assertEquals("user@example.com", account.email());
        assertEquals("hashed-pw", account.passwordHash());
        assertNull(account.lastLoginAt());
        assertNotNull(account.createdAt());
        assertNotNull(account.updatedAt());
    }

    @Test
    @DisplayName("Should implement Entity interface")
    void shouldImplementEntityInterface() {
        var account = new LocalAccount("id", "userId", "email@example.com", "hash");
        assertTrue(account instanceof Entity);
    }

    @Test
    @DisplayName("hasPassword should return true when passwordHash is present")
    void hasPasswordShouldReturnTrueWhenPresent() {
        var account = new LocalAccount("id", "userId", "email@example.com", "hashed");
        assertTrue(account.hasPassword());
    }

    @Test
    @DisplayName("hasPassword should return false when passwordHash is null")
    void hasPasswordShouldReturnFalseWhenNull() {
        var account = new LocalAccount("id", "userId", "email@example.com", null,
                null, LocalDateTime.now(), LocalDateTime.now());
        assertFalse(account.hasPassword());
    }

    @Test
    @DisplayName("hasPassword should return false when passwordHash is blank")
    void hasPasswordShouldReturnFalseWhenBlank() {
        var account = new LocalAccount("id", "userId", "email@example.com", "  ",
                null, LocalDateTime.now(), LocalDateTime.now());
        assertFalse(account.hasPassword());
    }

    @Test
    @DisplayName("withLastLoginAt should return updated account preserving other fields")
    void withLastLoginAtShouldUpdateTimestamp() {
        var account = new LocalAccount("id", "userId", "email@example.com", "hashed");
        assertNull(account.lastLoginAt());

        var loginAt = LocalDateTime.of(2024, 6, 15, 10, 30);
        var updated = account.withLastLoginAt(loginAt);

        assertEquals(loginAt, updated.lastLoginAt());
        assertEquals(account.id(), updated.id());
        assertEquals(account.email(), updated.email());
        assertEquals(account.passwordHash(), updated.passwordHash());
    }

    @Test
    @DisplayName("withNewPassword should return updated account with new password hash")
    void withNewPasswordShouldUpdatePasswordHash() {
        var account = new LocalAccount("id", "userId", "email@example.com", "old-hash");
        var updated = account.withNewPassword("new-hash");

        assertEquals("new-hash", updated.passwordHash());
        assertEquals(account.id(), updated.id());
        assertEquals(account.email(), updated.email());
    }

    @Test
    @DisplayName("withNewEmail should return updated account with new email")
    void withNewEmailShouldUpdateEmail() {
        var account = new LocalAccount("id", "userId", "old@example.com", "hashed");
        var updated = account.withNewEmail("new@example.com");

        assertEquals("new@example.com", updated.email());
        assertEquals(account.id(), updated.id());
        assertEquals(account.passwordHash(), updated.passwordHash());
    }

    @Test
    @DisplayName("withUpdatedTimestamp should return account with refreshed updatedAt")
    void withUpdatedTimestampShouldRefreshUpdatedAt() {
        var account = new LocalAccount("id", "userId", "email@example.com", "hashed");
        var updated = account.withUpdatedTimestamp();

        assertNotNull(updated.updatedAt());
        assertEquals(account.id(), updated.id());
        assertEquals(account.email(), updated.email());
    }
}
