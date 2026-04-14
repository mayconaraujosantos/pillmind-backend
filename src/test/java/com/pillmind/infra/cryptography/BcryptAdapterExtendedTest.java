package com.pillmind.infra.cryptography;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("BcryptAdapter - extended")
class BcryptAdapterExtendedTest {

    private BcryptAdapter sut;

    @BeforeEach
    void setUp() {
        sut = new BcryptAdapter(10);
    }

    @Test
    @DisplayName("Should hash a password to a non-empty string")
    void shouldHashPassword() {
        var hashed = sut.hash("myPassword");
        assertNotNull(hashed);
        assertFalse(hashed.isBlank());
    }

    @Test
    @DisplayName("Hashed value should differ from the plain text")
    void hashedValueShouldDifferFromPlainText() {
        var plain = "password123";
        var hashed = sut.hash(plain);
        assertNotEquals(plain, hashed);
    }

    @Test
    @DisplayName("Two hashes of the same password should differ (unique salts)")
    void twoHashesShouldDifferDueToSalt() {
        var hash1 = sut.hash("samePassword");
        var hash2 = sut.hash("samePassword");
        assertNotEquals(hash1, hash2);
    }

    @Test
    @DisplayName("compare should return true for matching password and hash")
    void compareShouldReturnTrueForMatchingPair() {
        var plain = "correctPassword";
        var hashed = sut.hash(plain);
        assertTrue(sut.compare(plain, hashed));
    }

    @Test
    @DisplayName("compare should return false for wrong password")
    void compareShouldReturnFalseForWrongPassword() {
        var hashed = sut.hash("correctPassword");
        assertFalse(sut.compare("wrongPassword", hashed));
    }

    @Test
    @DisplayName("compare should return false for empty string against valid hash")
    void compareShouldReturnFalseForEmptyPassword() {
        var hashed = sut.hash("somePassword");
        assertFalse(sut.compare("", hashed));
    }

    @Test
    @DisplayName("Hashed value should start with bcrypt prefix")
    void hashedValueShouldStartWithBcryptPrefix() {
        var hashed = sut.hash("password");
        assertTrue(hashed.startsWith("$2a$") || hashed.startsWith("$2b$") || hashed.startsWith("$2y$"));
    }

    @Test
    @DisplayName("Should use the provided salt rounds")
    void shouldUseProvidedSaltRounds() {
        var adapter10 = new BcryptAdapter(10);
        var hashed = adapter10.hash("password");
        // BCrypt hash encodes the cost factor, e.g. "$2a$10$..."
        assertTrue(hashed.contains("$10$"));
    }
}
