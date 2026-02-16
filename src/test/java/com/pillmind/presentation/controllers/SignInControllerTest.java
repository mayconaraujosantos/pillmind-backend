package com.pillmind.presentation.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.pillmind.domain.errors.UnauthorizedException;
import com.pillmind.domain.errors.ValidationException;
import com.pillmind.domain.models.Gender;
import com.pillmind.domain.models.User;
import com.pillmind.domain.usecases.LocalAuthentication;
import com.pillmind.presentation.handlers.ErrorHandlers;
import com.pillmind.presentation.validators.SignInValidation;

import io.javalin.testtools.JavalinTest;

/**
 * Testes para SignInController - Nova estrutura
 */
public class SignInControllerTest {

  private User makeUser() {
    return new User("user-id", "John Doe", "john@example.com", LocalDate.of(1990, 5, 15), Gender.MALE, null, true, 
                   LocalDateTime.now(), LocalDateTime.now());
  }

  @Test
  public void shouldReturn200WithAccessTokenOnSuccess() {
    var localAuthentication = mock(LocalAuthentication.class);
    var validator = mock(SignInValidation.class);

    var user = makeUser();
    when(localAuthentication.execute(any(LocalAuthentication.Params.class)))
        .thenReturn(new LocalAuthentication.Result("access-token-123", user));

    JavalinTest.test((app, client) -> {
      ErrorHandlers.configure(app);
      app.post("/api/signin", new SignInController(localAuthentication, validator)::handle);

      var response = client.post("/api/signin", """
          {
            "email": "valid@example.com",
            "password": "validPassword123"
          }
          """);

      assertEquals(200, response.code());
      assertTrue(response.body().string().contains("access-token-123"));
      verify(localAuthentication).execute(any(LocalAuthentication.Params.class));
    });
  }

  @Test
  public void shouldReturn401WhenCredentialsAreInvalid() {
    var localAuthentication = mock(LocalAuthentication.class);
    var validator = mock(SignInValidation.class);

    when(localAuthentication.execute(any(LocalAuthentication.Params.class)))
        .thenThrow(new UnauthorizedException("Email ou senha inválidos"));

    JavalinTest.test((app, client) -> {
      ErrorHandlers.configure(app);
      app.post("/api/signin", new SignInController(localAuthentication, validator)::handle);

      try (var response = client.post("/api/signin", """
          {
            "email": "invalid@example.com",
            "password": "wrongPassword"
          }
          """)) {

        assertEquals(401, response.code());
      }
    });
  }

  @Test
  public void shouldReturn400WhenRequestIsInvalid() {
    var localAuthentication = mock(LocalAuthentication.class);
    var validator = mock(SignInValidation.class);

    // Quando a validação for chamada com qualquer request, lança exceção para
    // simular payload inválido
    doThrow(new ValidationException("Password is required")).when(validator).validate(any());

    JavalinTest.test((app, client) -> {
      ErrorHandlers.configure(app);
      app.post("/api/signin", new SignInController(localAuthentication, validator)::handle);
      var payload = """
          {
            "email": "test@example.com",
            "password": ""
          }
          """;
      try (var response = client.post("/api/signin", payload)) {

        assertEquals(400, response.code());
      }
      verify(localAuthentication, never()).execute(any());
    });
  }
}
