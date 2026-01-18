package com.pillmind.presentation.controllers;

import com.pillmind.domain.usecases.Authentication;
import com.pillmind.presentation.validators.SignInValidation;
import io.javalin.testtools.JavalinTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes para SignInController
 */
public class SignInControllerTest {
  @Test
  public void shouldReturn200WithAccessTokenOnSuccess() {
    var authentication = mock(Authentication.class);
    var validator = mock(SignInValidation.class);

    when(authentication.execute(any(Authentication.Params.class)))
        .thenReturn(new Authentication.Result("access-token-123", "account-id"));

    JavalinTest.test((app, client) -> {
      app.post("/api/signin", new SignInController(authentication, validator)::handle);

      var response = client.post("/api/signin", """
          {
            "email": "valid@example.com",
            "password": "validPassword123"
          }
          """);

      assertEquals(200, response.code());
      assertTrue(response.body().string().contains("access-token-123"));
      verify(authentication).execute(any(Authentication.Params.class));
    });
  }

  @Test
  public void shouldReturn401WhenCredentialsAreInvalid() {
    var authentication = mock(Authentication.class);
    var validator = mock(SignInValidation.class);

    when(authentication.execute(any(Authentication.Params.class)))
        .thenThrow(new RuntimeException("Invalid credentials"));

    JavalinTest.test((app, client) -> {
      app.post("/api/signin", new SignInController(authentication, validator)::handle);

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
    var authentication = mock(Authentication.class);
    var validator = mock(SignInValidation.class);

    // Quando a validação for chamada com qualquer request, lança exceção para simular payload inválido
    doThrow(new RuntimeException("Password is required")).when(validator).validate(any());

    JavalinTest.test((app, client) -> {
      app.post("/api/signin", new SignInController(authentication, validator)::handle);
      var payload = """
        {
          "email": "test@example.com",
          "password": ""
        }
        """;
      try (var response = client.post("/api/signin", payload)) {

        assertEquals(400, response.code());
      }
      verify(authentication, never()).execute(any());
    });
  }
}
