package com.pillmind.domain.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.pillmind.data.protocols.cryptography.Encrypter;
import com.pillmind.data.protocols.cryptography.HashComparer;
import com.pillmind.data.protocols.db.LoadAccountByEmailRepository;
import com.pillmind.data.usecases.DbAuthentication;
import com.pillmind.domain.models.Account;

/**
 * Testes para o caso de uso Authentication
 */
class AuthenticationTest {
  @Test
  void shouldReturnAuthenticationResultWithAccessToken() {
    var loadAccountByEmailRepository = mock(LoadAccountByEmailRepository.class);
    var hashComparer = mock(HashComparer.class);
    var encrypter = mock(Encrypter.class);
    var sut = new DbAuthentication(loadAccountByEmailRepository, hashComparer, encrypter);

    var account = new Account("account-id", "Name", "valid@example.com", "hashedPassword", false);
    when(loadAccountByEmailRepository.loadByEmail("valid@example.com"))
        .thenReturn(Optional.of(account));
    when(hashComparer.compare("validPassword", "hashedPassword"))
        .thenReturn(true);
    when(encrypter.encrypt("account-id"))
        .thenReturn("access-token");

    var params = new Authentication.Params("valid@example.com", "validPassword");
    var result = sut.execute(params);

    assertNotNull(result);
    assertEquals("access-token", result.accessToken());
    assertEquals("account-id", result.accountId());
    verify(hashComparer).compare("validPassword", "hashedPassword");
    verify(encrypter).encrypt("account-id");
  }

  @Test
  void shouldThrowIfEmailNotFound() {
    var loadAccountByEmailRepository = mock(LoadAccountByEmailRepository.class);
    var hashComparer = mock(HashComparer.class);
    var encrypter = mock(Encrypter.class);
    var sut = new DbAuthentication(loadAccountByEmailRepository, hashComparer, encrypter);

    when(loadAccountByEmailRepository.loadByEmail("notfound@example.com"))
        .thenReturn(Optional.empty());

    var params = new Authentication.Params("notfound@example.com", "password");

    assertThrows(RuntimeException.class, () -> sut.execute(params));
    verify(hashComparer, never()).compare(anyString(), anyString());
  }

  @Test
  void shouldThrowIfPasswordIsInvalid() {
    var loadAccountByEmailRepository = mock(LoadAccountByEmailRepository.class);
    var hashComparer = mock(HashComparer.class);
    var encrypter = mock(Encrypter.class);
    var sut = new DbAuthentication(loadAccountByEmailRepository, hashComparer, encrypter);

    var account = new Account("account-id", "Name", "valid@example.com", "hashedPassword", false);
    when(loadAccountByEmailRepository.loadByEmail("valid@example.com"))
        .thenReturn(Optional.of(account));
    when(hashComparer.compare("wrongPassword", "hashedPassword"))
        .thenReturn(false);

    var params = new Authentication.Params("valid@example.com", "wrongPassword");

    assertThrows(RuntimeException.class, () -> sut.execute(params));
    verify(encrypter, never()).encrypt(anyString());
  }

  @Test
  void shouldThrowIfAccountIsGoogleAccount() {
    var loadAccountByEmailRepository = mock(LoadAccountByEmailRepository.class);
    var hashComparer = mock(HashComparer.class);
    var encrypter = mock(Encrypter.class);
    var sut = new DbAuthentication(loadAccountByEmailRepository, hashComparer, encrypter);

    var account = new Account("account-id", "Name", "google@example.com", null, true);
    when(loadAccountByEmailRepository.loadByEmail("google@example.com"))
        .thenReturn(Optional.of(account));

    var params = new Authentication.Params("google@example.com", "password");

    assertThrows(RuntimeException.class, () -> sut.execute(params));
    verify(hashComparer, never()).compare(anyString(), anyString());
  }
}
