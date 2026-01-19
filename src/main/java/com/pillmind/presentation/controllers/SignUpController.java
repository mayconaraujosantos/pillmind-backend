package com.pillmind.presentation.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pillmind.domain.usecases.AddAccount;
import com.pillmind.presentation.helpers.HttpHelper;
import com.pillmind.presentation.protocols.Controller;
import com.pillmind.presentation.protocols.Validation;

import io.javalin.http.Context;

/**
 * Controller para Sign Up (cadastro de usuário)
 */
public class SignUpController implements Controller {
  private static final Logger logger = LoggerFactory.getLogger(SignUpController.class);
  private final AddAccount addAccount;
  private final Validation<SignUpRequest> validation;
  private final ObjectMapper objectMapper;

  public SignUpController(AddAccount addAccount, Validation<SignUpRequest> validation) {
    this.addAccount = addAccount;
    this.validation = validation;
    this.objectMapper = new ObjectMapper();
  }

  @Override
  public void handle(Context ctx) {
    try {
      var request = objectMapper.readValue(ctx.body(), SignUpRequest.class);

      validation.validate(request);

      var params = new AddAccount.Params(
          request.name(),
          request.email(),
          request.password(),
          request.googleAccount() != null && request.googleAccount());

      var account = addAccount.execute(params);

      HttpHelper.created(ctx, new SignUpResponse(
          account.id(),
          account.name(),
          account.email(),
          account.googleAccount()));
    } catch (RuntimeException e) {
      HttpHelper.badRequest(ctx, e.getMessage());
    } catch (Exception e) {
      logger.error("Unexpected error in SignUpController: {}", e.getMessage(), e);
      HttpHelper.serverError(ctx, "Erro interno do servidor");
    }
  }

  public record SignUpRequest(
      String name,
      String email,
      String password,
      Boolean googleAccount) {
  }

  public record SignUpResponse(
      String id,
      String name,
      String email,
      boolean googleAccount) {
  }
}
