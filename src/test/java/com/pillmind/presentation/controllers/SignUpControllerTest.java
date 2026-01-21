package com.pillmind.presentation.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.pillmind.domain.models.Account;
import com.pillmind.domain.usecases.AddAccount;
import com.pillmind.presentation.protocols.Validation;

import io.javalin.testtools.JavalinTest;

class SignUpControllerTest {
  public LocalDateTime createdAt() {
    return LocalDateTime.of(2024, 1, 1, 12, 0);
  }

  public LocalDateTime updatedAt() {
    return LocalDateTime.now();
  }

  private Account makedAccount(String id, String name, String email, boolean googleAccount) {
    return new Account(id, name, email, null, googleAccount, null, null, null, createdAt(), updatedAt());
  }

  @Test
  @DisplayName("Should return 201 with account data on successful user creation")
  void shouldReturn201WithAccountDataOnSuccess() {
    var addAccount = mock(AddAccount.class);
    var validation = mock(Validation.class);

    var account = makedAccount("account-id", "John Doe", "john@example.com", false);
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
      verify(validation).validate(any(Object.class));
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
