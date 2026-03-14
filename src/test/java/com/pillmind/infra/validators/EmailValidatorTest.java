package com.pillmind.infra.validators;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Testes para EmailValidator
 */
class EmailValidatorTest {
    @Test
    void testValidEmails() {
        assertTrue(EmailValidator.isValid("test@example.com"));
        assertTrue(EmailValidator.isValid("user.name@domain.co.uk"));
    }

    @Test
    void testInvalidEmails() {
        assertFalse(EmailValidator.isValid("invalid"));
        assertFalse(EmailValidator.isValid("@example.com"));
        assertFalse(EmailValidator.isValid("test@"));
        assertFalse(EmailValidator.isValid(null));
        assertFalse(EmailValidator.isValid(""));
    }
}
