package com.pillmind.presentation.helpers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("LogSanitizer")
class LogSanitizerTest {

    @Test
    @DisplayName("Should mask password field in JSON body")
    void shouldMaskPasswordField() {
        var body = "{\"email\":\"user@example.com\",\"password\":\"secret123\"}";
        var result = LogSanitizer.sanitizeRequestBody(body);

        assertNotNull(result);
        assertFalse(result.contains("secret123"));
        assertTrue(result.contains("***"));
        assertTrue(result.contains("user@example.com"));
    }

    @Test
    @DisplayName("Should mask token field in JSON body")
    void shouldMaskTokenField() {
        var body = "{\"token\":\"my-jwt-token\",\"userId\":\"123\"}";
        var result = LogSanitizer.sanitizeRequestBody(body);

        assertFalse(result.contains("my-jwt-token"));
        assertTrue(result.contains("***"));
        assertTrue(result.contains("123"));
    }

    @Test
    @DisplayName("Should mask accessToken field in JSON body")
    void shouldMaskAccessTokenField() {
        var body = "{\"accessToken\":\"Bearer abc123\"}";
        var result = LogSanitizer.sanitizeRequestBody(body);

        assertFalse(result.contains("Bearer abc123"));
        assertTrue(result.contains("***"));
    }

    @Test
    @DisplayName("Should return placeholder for null body")
    void shouldReturnPlaceholderForNull() {
        var result = LogSanitizer.sanitizeRequestBody(null);
        assertEquals("[body vazio]", result);
    }

    @Test
    @DisplayName("Should return placeholder for blank body")
    void shouldReturnPlaceholderForBlankBody() {
        var result = LogSanitizer.sanitizeRequestBody("   ");
        assertEquals("[body vazio]", result);
    }

    @Test
    @DisplayName("Should return safe message for non-JSON body")
    void shouldReturnSafeMessageForNonJsonBody() {
        var result = LogSanitizer.sanitizeRequestBody("not valid json {{");
        assertEquals("[body não pode ser parseado]", result);
    }

    @Test
    @DisplayName("Should not modify non-sensitive fields")
    void shouldNotModifyNonSensitiveFields() {
        var body = "{\"name\":\"John\",\"email\":\"john@example.com\"}";
        var result = LogSanitizer.sanitizeRequestBody(body);

        assertTrue(result.contains("John"));
        assertTrue(result.contains("john@example.com"));
    }

    @Test
    @DisplayName("sanitizeSignUpLog should mask password value")
    void shouldMaskPasswordInSignUpLog() {
        var result = LogSanitizer.sanitizeSignUpLog("John Doe", "john@example.com");

        assertTrue(result.contains("John Doe"));
        assertTrue(result.contains("john@example.com"));
        // The literal password value is masked, not the JSON key name
        assertTrue(result.contains("\"password\":\"***\""));
    }

    @Test
    @DisplayName("sanitizeSignInLog should mask password")
    void shouldMaskPasswordInSignInLog() {
        var result = LogSanitizer.sanitizeSignInLog("john@example.com");

        assertTrue(result.contains("john@example.com"));
        assertTrue(result.contains("***"));
    }

    @Test
    @DisplayName("Should handle newline injection in names")
    void shouldSanitizeNewlineInjection() {
        var result = LogSanitizer.sanitizeSignUpLog("John\nDoe", "john@example.com");

        assertFalse(result.contains("\n"));
    }
}
