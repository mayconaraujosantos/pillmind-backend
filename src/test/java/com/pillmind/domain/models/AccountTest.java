package com.pillmind.domain.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Testes para a entidade Account
 */
class AccountTest {
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
    assertEquals(AuthProvider.LOCAL, account.authProvider());
    assertFalse(account.emailVerified());
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
    assertEquals(AuthProvider.GOOGLE, account.authProvider());
    assertTrue(account.emailVerified()); // Google accounts are verified by default
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

  @Test
  public void shouldDetectOAuth2Accounts() {
    var localAccount = new Account(
        "account-local",
        "Local User",
        "local@example.com",
        "password",
        false);

    var googleAccount = new Account(
        "account-google",
        "Google User",
        "google@example.com",
        null,
        true);

    assertFalse(localAccount.isOAuth2Account());
    assertTrue(localAccount.isLocalAccount());
    
    assertTrue(googleAccount.isOAuth2Account());
    assertFalse(googleAccount.isLocalAccount());
  }

  @Test
  public void shouldUpdateEmailVerificationStatus() {
    var account = new Account(
        "account-id",
        "User Name",
        "user@example.com",
        "password",
        false);

    assertFalse(account.emailVerified());

    var verifiedAccount = account.withEmailVerified(true);
    
    assertTrue(verifiedAccount.emailVerified());
    assertEquals(account.id(), verifiedAccount.id());
    assertEquals(account.email(), verifiedAccount.email());
  }

  @Test
  public void shouldUpdateAuthProvider() {
    var account = new Account(
        "account-id",
        "User Name",
        "user@example.com",
        "password",
        false);

    assertEquals(AuthProvider.LOCAL, account.authProvider());

    var googleAccount = account.withAuthProvider(AuthProvider.GOOGLE);
    
    assertEquals(AuthProvider.GOOGLE, googleAccount.authProvider());
    assertEquals(account.id(), googleAccount.id());
    assertEquals(account.email(), googleAccount.email());
  }
}
