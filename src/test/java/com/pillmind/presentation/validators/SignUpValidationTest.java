package com.pillmind.presentation.validators;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.pillmind.domain.errors.ValidationException;
import com.pillmind.presentation.controllers.SignUpController.SignUpRequest;

@DisplayName("SignUpValidation")
class SignUpValidationTest {

    private SignUpValidation sut;

    @BeforeEach
    void setUp() {
        sut = new SignUpValidation();
    }

    private SignUpRequest makeRequest(String name, String email, String password) {
        return new SignUpRequest(name, email, password, null, null, null);
    }

    @Test
    @DisplayName("Should pass validation with valid input")
    void shouldPassWithValidInput() {
        assertDoesNotThrow(() -> sut.validate(makeRequest("John Doe", "john@example.com", "password123")));
    }

    @Test
    @DisplayName("Should throw when name is null")
    void shouldThrowWhenNameIsNull() {
        var ex = assertThrows(ValidationException.class,
                () -> sut.validate(makeRequest(null, "john@example.com", "password123")));
        assertEquals("O campo 'name' é obrigatório", ex.getMessage());
    }

    @Test
    @DisplayName("Should throw when name is blank")
    void shouldThrowWhenNameIsBlank() {
        assertThrows(ValidationException.class,
                () -> sut.validate(makeRequest("  ", "john@example.com", "password123")));
    }

    @Test
    @DisplayName("Should throw when email is null")
    void shouldThrowWhenEmailIsNull() {
        var ex = assertThrows(ValidationException.class,
                () -> sut.validate(makeRequest("John", null, "password123")));
        assertEquals("O campo 'email' é obrigatório", ex.getMessage());
    }

    @Test
    @DisplayName("Should throw when email is blank")
    void shouldThrowWhenEmailIsBlank() {
        assertThrows(ValidationException.class,
                () -> sut.validate(makeRequest("John", "  ", "password123")));
    }

    @Test
    @DisplayName("Should throw when email format is invalid")
    void shouldThrowWhenEmailFormatIsInvalid() {
        var ex = assertThrows(ValidationException.class,
                () -> sut.validate(makeRequest("John", "not-an-email", "password123")));
        assertEquals("Formato de email inválido", ex.getMessage());
    }

    @Test
    @DisplayName("Should throw when password is null")
    void shouldThrowWhenPasswordIsNull() {
        var ex = assertThrows(ValidationException.class,
                () -> sut.validate(makeRequest("John", "john@example.com", null)));
        assertEquals("O campo 'password' é obrigatório", ex.getMessage());
    }

    @Test
    @DisplayName("Should throw when password is blank")
    void shouldThrowWhenPasswordIsBlank() {
        assertThrows(ValidationException.class,
                () -> sut.validate(makeRequest("John", "john@example.com", "  ")));
    }

    @Test
    @DisplayName("Should throw when password is shorter than 6 characters")
    void shouldThrowWhenPasswordTooShort() {
        var ex = assertThrows(ValidationException.class,
                () -> sut.validate(makeRequest("John", "john@example.com", "abc")));
        assertEquals("A senha deve ter no mínimo 6 caracteres", ex.getMessage());
    }

    @Test
    @DisplayName("Should pass with password of exactly 6 characters")
    void shouldPassWithPasswordOfExactly6Chars() {
        assertDoesNotThrow(() -> sut.validate(makeRequest("John", "john@example.com", "abc123")));
    }
}
