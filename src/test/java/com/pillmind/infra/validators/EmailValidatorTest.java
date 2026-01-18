package com.pillmind.infra.validators;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para EmailValidator
 */
public class EmailValidatorTest {
    @Test
    public void testValidEmails() {
        assertTrue(EmailValidator.isValid("test@example.com"));
        assertTrue(EmailValidator.isValid("user.name@domain.co.uk"));
    }

    @Test
    public void testInvalidEmails() {
        assertFalse(EmailValidator.isValid("invalid"));
        assertFalse(EmailValidator.isValid("@example.com"));
        assertFalse(EmailValidator.isValid("test@"));
        assertFalse(EmailValidator.isValid(null));
        assertFalse(EmailValidator.isValid(""));
    }
}
