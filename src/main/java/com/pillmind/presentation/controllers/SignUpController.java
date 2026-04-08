package com.pillmind.presentation.controllers;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pillmind.domain.errors.ValidationException;
import com.pillmind.domain.models.Gender;
import com.pillmind.domain.usecases.CreateLocalAccount;
import com.pillmind.domain.usecases.CreateLocalAccount.Params;
import com.pillmind.domain.usecases.CreateLocalAccount.Result;
import com.pillmind.presentation.helpers.HttpHelper;
import com.pillmind.presentation.protocols.Controller;
import com.pillmind.presentation.protocols.Validation;

import io.javalin.http.Context;

/**
 * Controller para Sign Up (cadastro de usuário) - Nova estrutura
 */
public class SignUpController implements Controller {
  private static final Logger logger = LoggerFactory.getLogger(SignUpController.class);
  private final CreateLocalAccount createLocalAccount;
  private final Validation<SignUpRequest> validation;
  private final ObjectMapper objectMapper;

  public SignUpController(CreateLocalAccount createLocalAccount, Validation<SignUpRequest> validation) {
    this.createLocalAccount = createLocalAccount;
    this.validation = validation;
    this.objectMapper = new ObjectMapper();
    this.objectMapper.registerModule(new JavaTimeModule());
  }

  @Override
  public void handle(Context ctx) {
    try {
      var request = objectMapper.readValue(ctx.body(), SignUpRequest.class);

      validation.validate(request);

      var params = getParams(request);

      var result = createLocalAccount.execute(params);
      logger.debug("✓ User and LocalAccount created: userId={}", result.user().id());

      var response = getResponse(result);
      
      logger.debug("✓ Sending response: email={}, id={}", 
          result.user().email(), result.user().id());
      
      HttpHelper.created(ctx, response);
    } catch (JsonProcessingException e) {
      throw new ValidationException("Formato JSON inválido na requisição", e);
    }
  }

  private SignUpResponse getResponse(Result result) {
    var response = new SignUpResponse(
        result.user().id(),
        result.user().name(),
        result.user().email(),
        result.user().dateOfBirth(),
        result.user().gender() != null ? result.user().gender().name() : null,
        result.user().pictureUrl(),
        result.user().emailVerified());
    return response;
  }

  private Params getParams(SignUpRequest request) {
    var params = new CreateLocalAccount.Params(
        request.name(),
        request.email(),
        request.password(),
        request.dateOfBirth(),
        Gender.fromString(request.gender()),
        request.pictureUrl());
    return params;
  }

  public record SignUpRequest(
      String name,
      String email,
      String password,
      @JsonFormat(pattern = "yyyy-MM-dd")
      LocalDate dateOfBirth,
      String gender,
      String pictureUrl) {
  }

  public record SignUpResponse(
      String id,
      String name,
      String email,
      @JsonFormat(pattern = "yyyy-MM-dd")
      LocalDate dateOfBirth,
      String gender,
      String pictureUrl,
      boolean emailVerified) {
  }
}
