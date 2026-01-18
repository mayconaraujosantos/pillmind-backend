package com.pillmind.domain.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Testes para a entidade Account
 */
public class AccountTest {
  @Test
  public void shouldCreateAccountWithValidData() {
    var account = new Account(
        "account-id-123",
        "John Doe",
        "john@example.com",
        "hashed-password",
        false
    );

    assertEquals("account-id-123", account.id());
    assertEquals("John Doe", account.name());
    assertEquals("john@example.com", account.email());
    assertEquals("hashed-password", account.password());
    assertFalse(account.googleAccount());
  }

  @Test
  public void shouldCreateGoogleAccount() {
    var account = new Account(
        "account-id-456",
        "Jane Doe",
        "jane@example.com",
        null,
        true);

    assertTrue(account.googleAccount());
    assertNull(account.password());
  }

  @Test
  public void shouldImplementEntityInterface() {
    var account = new Account(
        "account-id-789",
        "Test User",
        "test@example.com",
        "password",
        false);

    assertTrue(account instanceof Entity);
    assertEquals("account-id-789", account.id());
  }
}
