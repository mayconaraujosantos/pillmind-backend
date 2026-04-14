package com.pillmind.infra.cryptography;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("JwtAdapter - extended")
class JwtAdapterExtendedTest {

    private static final String SECRET = "test-secret-key-min-256-bits-for-hmac-sha-256-algorithm";
    private JwtAdapter sut;

    @BeforeEach
    void setUp() {
        sut = new JwtAdapter(SECRET, 3_600_000L); // 1 hour
    }

    @Test
    @DisplayName("Should encrypt a subject and return a non-empty token")
    void shouldEncryptSubject() {
        var token = sut.encrypt("user-id-123");
        assertNotNull(token);
        assertNotEquals("user-id-123", token);
    }

    @Test
    @DisplayName("Should decrypt the encrypted token back to the original subject")
    void shouldDecryptToken() {
        var subject = "user-id-456";
        var token = sut.encrypt(subject);
        assertEquals(subject, sut.decrypt(token));
    }

    @Test
    @DisplayName("Two tokens for the same subject should be different when issued at different seconds")
    void twoTokensForSameSubjectShouldDiffer() throws InterruptedException {
        var token1 = sut.encrypt("user-id");
        Thread.sleep(1100); // JWT timestamps have second-level precision
        var token2 = sut.encrypt("user-id");
        assertNotEquals(token1, token2);
    }

    @Test
    @DisplayName("Should throw when decrypting an invalid token")
    void shouldThrowWhenDecryptingInvalidToken() {
        assertThrows(Exception.class, () -> sut.decrypt("not.a.valid.token"));
    }

    @Test
    @DisplayName("Should throw when decrypting a token signed with a different secret")
    void shouldThrowWhenDecryptingTokenWithDifferentSecret() {
        var otherAdapter = new JwtAdapter("totally-different-secret-key-min-256-bits-here", 3_600_000L);
        var token = otherAdapter.encrypt("user-id");

        assertThrows(Exception.class, () -> sut.decrypt(token));
    }

    @Test
    @DisplayName("Should throw when token is expired")
    void shouldThrowWhenTokenIsExpired() throws InterruptedException {
        var expiredAdapter = new JwtAdapter(SECRET, 1L); // 1 ms expiry
        var token = expiredAdapter.encrypt("user-id");

        Thread.sleep(50); // wait for expiry

        assertThrows(Exception.class, () -> sut.decrypt(token));
    }
}
