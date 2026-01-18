package com.pillmind.presentation.controllers;

import com.pillmind.domain.models.Account;
import com.pillmind.domain.usecases.AddAccount;
import com.pillmind.presentation.helpers.HttpHelper;
import com.pillmind.presentation.protocols.Validation;
import io.javalin.http.Context;
import io.javalin.testtools.JavalinTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes para SignUpController
 */
class SignUpControllerTest {
  @Test
  void shouldReturn200WithAccountDataOnSuccess() {
    var addAccount = mock(AddAccount.class);
    var validation = mock(Validation.class);

    var account = new Account("account-id", "John Doe", "john@example.com", "hashed", false);
    when(addAccount.execute(any(AddAccount.Params.class))).thenReturn(account);

    JavalinTest.test((app, client) -> {
      app.post("/api/signup", new SignUpController(addAccount, validation)::handle);

      var response = client.post("/api/signup", """
          {
            "name": "John Doe",
            "email": "john@example.com",
            "password": "validPassword123",
            "googleAccount": false
          }
          """);

        assertEquals(201, response.code());
         assertTrue(response.body().string().contains("account-id"));
      verify(validation).validate(any());
      verify(addAccount).execute(any(AddAccount.Params.class));
    });
  }

  @Test
  void shouldReturn400WhenValidationFails() {
    var addAccount = mock(AddAccount.class);
    var validation = mock(Validation.class);

    doThrow(new RuntimeException("Validation error")).when(validation).validate(any());

    JavalinTest.test((app, client) -> {
      app.post("/api/signup", new SignUpController(addAccount, validation)::handle);

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
  void shouldReturn400WhenEmailAlreadyExists() {
    var addAccount = mock(AddAccount.class);
    var validation = mock(Validation.class);

    when(addAccount.execute(any(AddAccount.Params.class)))
        .thenThrow(new RuntimeException("Email already exists"));

    JavalinTest.test((app, client) -> {
      app.post("/api/signup", new SignUpController(addAccount, validation)::handle);

      var response = client.post("/api/signup", """
          {
            "name": "John Doe",
            "email": "existing@example.com",
            "password": "validPassword123",
            "googleAccount": false
          }
          """);

      assertEquals(400, response.code());
    });
  }
}
