package com.pillmind.presentation.validators;

import com.pillmind.domain.errors.ValidationException;
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
      throw new ValidationException("O campo 'name' é obrigatório");
    }

    if (input.email() == null || input.email().isBlank()) {
      throw new ValidationException("O campo 'email' é obrigatório");
    }

    if (!EmailValidator.isValid(input.email())) {
      throw new ValidationException("Formato de email inválido");
    }

    // Se não for conta Google, senha é obrigatória
    if ((input.googleAccount() == null || !input.googleAccount()) &&
        (input.password() == null || input.password().isBlank())) {
      throw new ValidationException("O campo 'password' é obrigatório");
    }

    if (input.password() != null && input.password().length() < 6) {
      throw new ValidationException("A senha deve ter no mínimo 6 caracteres");
    }
  }
}
