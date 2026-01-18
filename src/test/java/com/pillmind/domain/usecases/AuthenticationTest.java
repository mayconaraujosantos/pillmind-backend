package com.pillmind.domain.usecases;

import com.pillmind.data.protocols.cryptography.HashComparer;
import com.pillmind.data.protocols.cryptography.Encrypter;
import com.pillmind.data.protocols.db.LoadAccountByEmailRepository;
import com.pillmind.data.usecases.DbAuthentication;
import com.pillmind.domain.models.Account;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Testes para o caso de uso Authentication
 */
public class AuthenticationTest {
  @Test
  public void shouldReturnAuthenticationResultWithAccessToken() {
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
  public void shouldThrowIfEmailNotFound() {
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
  public void shouldThrowIfPasswordIsInvalid() {
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
  public void shouldThrowIfAccountIsGoogleAccount() {
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
