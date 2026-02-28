package com.pillmind.presentation.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.function.BiConsumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.pillmind.domain.errors.ConflictException;
import com.pillmind.domain.errors.ValidationException;
import com.pillmind.domain.models.Gender;
import com.pillmind.domain.models.User;
import com.pillmind.domain.usecases.CreateLocalAccount;
import com.pillmind.presentation.controllers.SignUpController.SignUpRequest;
import com.pillmind.presentation.handlers.ErrorHandlers;
import com.pillmind.presentation.protocols.Validation;
import com.pillmind.presentation.validators.SignUpValidation;

import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import io.javalin.testtools.HttpClient;

class SignUpControllerTest {

  private CreateLocalAccount createLocalAccount;
  private Validation<SignUpRequest> validation;
  private SignUpController signUpController;

  public LocalDateTime createdAt() {
    return LocalDateTime.of(2024, 1, 1, 12, 0);
  }

  public LocalDateTime updatedAt() {
    return LocalDateTime.now();
  }

  private User makeUser(String id, String name, String email) {
    return new User(id, name, email, LocalDate.of(1990, 5, 15), Gender.OTHER, null, true, createdAt(), updatedAt());
  }

  @BeforeEach
  void setUp() {
    createLocalAccount = mock(CreateLocalAccount.class);
    validation = mock(SignUpValidation.class);
    signUpController = new SignUpController(createLocalAccount, validation);
  }

  private void withSignUpApp(BiConsumer<Javalin, HttpClient> test) {
    JavalinTest.test((app, client) -> {
      ErrorHandlers.configure(app);
      app.post("/api/signup", signUpController::handle);
      test.accept(app, client);
    });
  }

  @Test
  @DisplayName("Should return 201 with user data on successful user creation")
  void shouldReturn201WithUserDataOnSuccess() {
    var user = makeUser("user-id", "John Doe", "john@example.com");
    when(createLocalAccount.execute(any(CreateLocalAccount.Params.class))).thenReturn(new CreateLocalAccount.Result(user, "local-account-id"));

    withSignUpApp((app, client) -> {
      var response = client.post("/api/signup", """
          {
            "name": "John Doe",
            "email": "john@example.com",
            "password": "validPassword123",
            "dateOfBirth": "1990-05-15",
            "gender": "OTHER"
          }
          """);

      assertEquals(201, response.code());
      verify(validation).validate(any(SignUpRequest.class));
      verify(createLocalAccount).execute(any(CreateLocalAccount.Params.class));
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
      verify(createLocalAccount, never()).execute(any());
    });
  }

  @Test
  void shouldReturn409WhenEmailAlreadyExists() {

    when(createLocalAccount.execute(any(CreateLocalAccount.Params.class)))
        .thenThrow(new ConflictException("Email already exists"));

    withSignUpApp((app, client) -> {
      var response = client.post("/api/signup", """
          {
            "name": "John Doe",
            "email": "existing@example.com",
            "password": "validPassword123",
            "dateOfBirth": "1990-05-15",
            "gender": "MALE"
          }
          """);

      assertEquals(409, response.code());
    });
  }
}
