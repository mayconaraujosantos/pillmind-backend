package com.pillmind.presentation.validators;

import com.pillmind.domain.errors.ValidationException;
import com.pillmind.infra.validators.EmailValidator;
import com.pillmind.presentation.controllers.SignInController;
import com.pillmind.presentation.protocols.Validation;

public class SignInValidation implements Validation<SignInController.SignInRequest> {

  @Override
  public void validate(SignInController.SignInRequest input) {
    // Validação básica de campos obrigatórios
    if (input.email() == null || input.email().isBlank()) {
      throw new ValidationException("O campo 'email' é obrigatório");
    }

    if (input.password() == null || input.password().isBlank()) {
      throw new ValidationException("O campo 'password' é obrigatório");
    }

    // Validação de formato de email
    if (!EmailValidator.isValid(input.email())) {
      throw new ValidationException("Formato de email inválido");
    }

    // ⚠️ SECURITY: Não validamos regras de negócio (tamanho da senha) no SignIn
    // para evitar user enumeration. Credenciais inválidas serão tratadas de
    // forma genérica pelo Authentication use case.
  }
}
