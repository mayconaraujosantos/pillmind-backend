package com.pillmind.presentation.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pillmind.domain.usecases.AddAccount;
import com.pillmind.domain.usecases.Authentication;
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
  private final Authentication authentication;
  private final Validation<SignUpRequest> validation;
  private final ObjectMapper objectMapper;

  public SignUpController(AddAccount addAccount, Authentication authentication, Validation<SignUpRequest> validation) {
    this.addAccount = addAccount;
    this.authentication = authentication;
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
          request.googleAccount() != null && request.googleAccount(),
          null,
          null);

      var account = addAccount.execute(params);
      logger.debug("✓ Account created: {}", account.id());

      // Auto-authenticate after signup
      var authParams = new Authentication.Params(request.email(), request.password());
      var authResult = authentication.execute(authParams);
      logger.debug("✓ Authentication result: {}", authResult.accessToken() != null ? "Token generated" : "No token");

      var response = new SignUpResponse(
          authResult.accessToken(),
          account.id(),
          account.name(),
          account.email());
      
      logger.debug("✓ Sending response: email={}, id={}, hasToken={}", 
          account.email(), account.id(), authResult.accessToken() != null);
      
      HttpHelper.created(ctx, response);
    } catch (RuntimeException e) {
      logger.error("✗ RuntimeException in SignUp: {}", e.getMessage(), e);
      HttpHelper.badRequest(ctx, e.getMessage());
    } catch (Exception e) {
      logger.error("✗ Unexpected exception in SignUp: {}", e.getMessage(), e);
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
      String accessToken,
      String id,
      String name,
      String email) {
  }
}
