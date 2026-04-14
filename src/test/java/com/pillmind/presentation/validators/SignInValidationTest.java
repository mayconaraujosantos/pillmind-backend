package com.pillmind.presentation.validators;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.pillmind.domain.errors.ValidationException;
import com.pillmind.presentation.controllers.SignInController.SignInRequest;

@DisplayName("SignInValidation")
class SignInValidationTest {

    private SignInValidation sut;

    @BeforeEach
    void setUp() {
        sut = new SignInValidation();
    }

    @Test
    @DisplayName("Should pass with valid email and password")
    void shouldPassWithValidInput() {
        assertDoesNotThrow(() -> sut.validate(new SignInRequest("user@example.com", "password123")));
    }

    @Test
    @DisplayName("Should throw when email is null")
    void shouldThrowWhenEmailIsNull() {
        var ex = assertThrows(ValidationException.class,
                () -> sut.validate(new SignInRequest(null, "password123")));
        assertEquals("O campo 'email' é obrigatório", ex.getMessage());
    }

    @Test
    @DisplayName("Should throw when email is blank")
    void shouldThrowWhenEmailIsBlank() {
        assertThrows(ValidationException.class,
                () -> sut.validate(new SignInRequest("  ", "password123")));
    }

    @Test
    @DisplayName("Should throw when password is null")
    void shouldThrowWhenPasswordIsNull() {
        var ex = assertThrows(ValidationException.class,
                () -> sut.validate(new SignInRequest("user@example.com", null)));
        assertEquals("O campo 'password' é obrigatório", ex.getMessage());
    }

    @Test
    @DisplayName("Should throw when password is blank")
    void shouldThrowWhenPasswordIsBlank() {
        assertThrows(ValidationException.class,
                () -> sut.validate(new SignInRequest("user@example.com", "  ")));
    }

    @Test
    @DisplayName("Should throw when email format is invalid")
    void shouldThrowWhenEmailFormatIsInvalid() {
        var ex = assertThrows(ValidationException.class,
                () -> sut.validate(new SignInRequest("not-an-email", "password123")));
        assertEquals("Formato de email inválido", ex.getMessage());
    }

    @Test
    @DisplayName("Should pass without enforcing password length (unlike signup)")
    void shouldNotEnforcePasswordLength() {
        // SignIn should not reject short passwords to avoid user enumeration
        assertDoesNotThrow(() -> sut.validate(new SignInRequest("user@example.com", "abc")));
    }
}
