package com.pillmind.presentation.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.function.BiConsumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.pillmind.domain.errors.ConflictException;
import com.pillmind.domain.errors.ValidationException;
import com.pillmind.domain.models.Account;
import com.pillmind.domain.usecases.AddAccount;
import com.pillmind.presentation.controllers.SignUpController.SignUpRequest;
import com.pillmind.presentation.handlers.ErrorHandlers;
import com.pillmind.presentation.protocols.Validation;
import com.pillmind.presentation.validators.SignUpValidation;
import com.pillmind.domain.usecases.Authentication;

import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import io.javalin.testtools.HttpClient;

class SignUpControllerTest {

  private AddAccount addAccount;
  private Authentication authentication;
  private Validation<SignUpRequest> validation;
  private SignUpController signUpController;

  public LocalDateTime createdAt() {
    return LocalDateTime.of(2024, 1, 1, 12, 0);
  }

  public LocalDateTime updatedAt() {
    return LocalDateTime.now();
  }

  private Account makedAccount(String id, String name, String email, boolean googleAccount) {
    return new Account(id, name, email, null, googleAccount, null, null, null, createdAt(), updatedAt());
  }



  @BeforeEach
  void setUp() {
    addAccount = mock(AddAccount.class);
    authentication = mock(Authentication.class);
    validation = mock(SignUpValidation.class);
    signUpController = new SignUpController(addAccount, authentication, validation);
  }

  private void withSignUpApp(BiConsumer<Javalin, HttpClient> test) {
    JavalinTest.test((app, client) -> {
      ErrorHandlers.configure(app);
      app.post("/api/signup", signUpController::handle);
      test.accept(app, client);
    });
  }


  @Test
  @DisplayName("Should return 201 with account data on successful user creation")
  void shouldReturn201WithAccountDataOnSuccess() {
    var account = makedAccount("account-id", "John Doe", "john@example.com", false);
    when(addAccount.execute(any(AddAccount.Params.class))).thenReturn(account);
    when(authentication.execute(any(Authentication.Params.class))).thenReturn(new Authentication.Result("token-123", "account-id"));

    withSignUpApp((app, client) -> {
      var response = client.post("/api/signup", """
          {
            "name": "John Doe",
            "email": "john@example.com",
            "password": "validPassword123",
            "googleAccount": false
          }
          """);

      assertEquals(201, response.code());
      verify(validation).validate(any(SignUpRequest.class));
      verify(addAccount).execute(any(AddAccount.Params.class));
    });
  }

  @Test
  void shouldReturn400WhenValidationFails() {

    doThrow(new ValidationException("Validation error")).when(validation).validate(any());

    withSignUpApp((app, client) -> {
      var response = client.post("/api/signup", """
          {
            "name": "",
            "email": "invalid",
            "password": "123"
          }
          """);

      assertEquals(400, response.code());
      verify(addAccount, never()).execute(any());
    });
  }

  @Test
  void shouldReturn409WhenEmailAlreadyExists() {

    when(addAccount.execute(any(AddAccount.Params.class)))
        .thenThrow(new ConflictException("Email already exists"));

    withSignUpApp((app, client) -> {
      var response = client.post("/api/signup", """
          {
            "name": "John Doe",
            "email": "existing@example.com",
            "password": "validPassword123",
            "googleAccount": false
          }
          """);

      assertEquals(409, response.code());
    });
  }

}
