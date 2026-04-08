package com.pillmind.presentation.controllers;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pillmind.domain.errors.ValidationException;
import com.pillmind.domain.usecases.LocalAuthentication;
import com.pillmind.presentation.helpers.HttpHelper;
import com.pillmind.presentation.protocols.Controller;
import com.pillmind.presentation.protocols.Validation;

import io.javalin.http.Context;

/**
 * Controller para Sign In (autenticação de usuário) - Nova estrutura
 */
public class SignInController implements Controller {
  private final LocalAuthentication localAuthentication;
  private final ObjectMapper objectMapper;
  private final Validation<SignInRequest> validator;

  public SignInController(LocalAuthentication localAuthentication, Validation<SignInRequest> validator) {
    this.localAuthentication = localAuthentication;
    this.validator = validator;
    this.objectMapper = new ObjectMapper();
    this.objectMapper.registerModule(new JavaTimeModule());
  }

  @Override
  public void handle(Context ctx) {
    try {
      var request = objectMapper.readValue(ctx.body(), SignInRequest.class);

      validator.validate(request);

      var params = new LocalAuthentication.Params(request.email(), request.password());
      var result = localAuthentication.execute(params);

      var response = new SignInResponse(
          result.accessToken(), 
          result.user().id(),
          result.user().name(),
          result.user().email(),
          result.user().dateOfBirth(),
          result.user().gender() != null ? result.user().gender().name() : null,
          result.user().pictureUrl(),
          result.user().emailVerified());

      HttpHelper.ok(ctx, response);
    } catch (JsonProcessingException e) {
      throw new ValidationException("Formato JSON inválido na requisição", e);
    }
  }

  public record SignInRequest(String email, String password) {
  }

  public record SignInResponse(
      String accessToken, 
      String userId,
      String name,
      String email,
      @JsonFormat(pattern = "yyyy-MM-dd")
      LocalDate dateOfBirth,
      String gender,
      String pictureUrl,
      boolean emailVerified) {
  }
}
