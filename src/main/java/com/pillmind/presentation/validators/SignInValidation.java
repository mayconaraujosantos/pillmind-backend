package com.pillmind.presentation.validators;

import com.pillmind.infra.validators.EmailValidator;
import com.pillmind.presentation.controllers.SignInController;
import com.pillmind.presentation.controllers.SignUpController;
import com.pillmind.presentation.protocols.Validation;

public class SignInValidation implements Validation<SignInController.SignInRequest> {

  @Override
  public void validate(SignInController.SignInRequest input) {
    // Validação básica de campos obrigatórios
    if (input.email() == null || input.email().isBlank()) {
      throw new RuntimeException("Email is required");
    }

    if (input.password() == null || input.password().isBlank()) {
      throw new RuntimeException("Password is required");
    }

    // Validação opcional de formato de email
    if (!EmailValidator.isValid(input.email())) {
      throw new RuntimeException("Invalid email format");
    }

    // Validação opcional de tamanho mínimo da senha
    if (input.password().length() < 6) {
      throw new RuntimeException("Password must be at least 6 characters");
    }
  }
}
