package com.pillmind.presentation.validators;

import com.pillmind.infra.validators.EmailValidator;
import com.pillmind.presentation.controllers.SignUpController;
import com.pillmind.presentation.protocols.Validation;

/**
 * Validador para requisições de Sign Up
 */
public class SignUpValidation implements Validation<SignUpController.SignUpRequest> {
  @Override
  public void validate(SignUpController.SignUpRequest input) {
    if (input.name() == null || input.name().isBlank()) {
      throw new RuntimeException("Name is required");
    }

    if (input.email() == null || input.email().isBlank()) {
      throw new RuntimeException("Email is required");
    }

    if (!EmailValidator.isValid(input.email())) {
      throw new RuntimeException("Invalid email format");
    }

    // Se não for conta Google, senha é obrigatória
    if ((input.googleAccount() == null || !input.googleAccount()) &&
        (input.password() == null || input.password().isBlank())) {
      throw new RuntimeException("Password is required");
    }

    if (input.password() != null && input.password().length() < 6) {
      throw new RuntimeException("Password must be at least 6 characters");
    }
  }
}
