package com.pillmind.presentation.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pillmind.domain.errors.ValidationException;
import com.pillmind.domain.usecases.Authentication;
import com.pillmind.presentation.helpers.HttpHelper;
import com.pillmind.presentation.protocols.Controller;
import com.pillmind.presentation.protocols.Validation;

import io.javalin.http.Context;

/**
 * Controller para Sign In (autenticação de usuário)
 */
public class SignInController implements Controller {
  private final Authentication authentication;
  private final ObjectMapper objectMapper;
  private final Validation<SignInRequest> validator;

  public SignInController(Authentication authentication, Validation<SignInRequest> validator) {
    this.authentication = authentication;
    this.validator = validator;
    this.objectMapper = new ObjectMapper();
  }

  @Override
  public void handle(Context ctx) {
    try {
      var request = objectMapper.readValue(ctx.body(), SignInRequest.class);

      validator.validate(request);

      var params = new Authentication.Params(request.email(), request.password());
      var result = authentication.execute(params);

      HttpHelper.ok(ctx, new SignInResponse(result.accessToken(), result.accountId()));
    } catch (JsonProcessingException e) {
      throw new ValidationException("Formato JSON inválido na requisição", e);
    }
  }

  public record SignInRequest(String email, String password) {
  }

  public record SignInResponse(String accessToken, String accountId) {
  }
}
