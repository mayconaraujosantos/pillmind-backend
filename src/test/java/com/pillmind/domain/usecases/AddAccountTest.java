package com.pillmind.domain.usecases;

import com.pillmind.data.protocols.cryptography.Hasher;
import com.pillmind.data.protocols.db.AddAccountRepository;
import com.pillmind.data.protocols.db.LoadAccountByEmailRepository;
import com.pillmind.data.usecases.DbAddAccount;
import com.pillmind.domain.models.Account;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Testes para o caso de uso AddAccount
 */
public class AddAccountTest {
  @Test
  public void shouldCallHasherWithCorrectPassword() {
    var hasher = mock(Hasher.class);
    var addAccountRepository = mock(AddAccountRepository.class);
    var loadAccountByEmailRepository = mock(LoadAccountByEmailRepository.class);
    var sut = new DbAddAccount(hasher, addAccountRepository, loadAccountByEmailRepository);

    when(loadAccountByEmailRepository.loadByEmail("valid@example.com"))
        .thenReturn(Optional.empty());
    when(hasher.hash("validPassword123"))
        .thenReturn("hashedPassword");
    when(addAccountRepository.add(any(Account.class)))
        .thenReturn(new Account("id", "Name", "valid@example.com", "hashedPassword", false));

    var params = new AddAccount.Params("Name", "valid@example.com", "validPassword123", false);
    sut.execute(params);

    verify(hasher).hash("validPassword123");
  }

  @Test
  public void shouldThrowIfEmailAlreadyExists() {
    var hasher = mock(Hasher.class);
    var addAccountRepository = mock(AddAccountRepository.class);
    var loadAccountByEmailRepository = mock(LoadAccountByEmailRepository.class);
    var sut = new DbAddAccount(hasher, addAccountRepository, loadAccountByEmailRepository);

    when(loadAccountByEmailRepository.loadByEmail("existing@example.com"))
        .thenReturn(Optional.of(new Account("id", "Name", "existing@example.com", "hash", false)));

    var params = new AddAccount.Params("Name", "existing@example.com", "password", false);

    assertThrows(RuntimeException.class, () -> sut.execute(params));
    verify(addAccountRepository, never()).add(any());
  }

  @Test
  public void shouldReturnAccountOnSuccess() {
    var hasher = mock(Hasher.class);
    var addAccountRepository = mock(AddAccountRepository.class);
    var loadAccountByEmailRepository = mock(LoadAccountByEmailRepository.class);
    var sut = new DbAddAccount(hasher, addAccountRepository, loadAccountByEmailRepository);

    when(loadAccountByEmailRepository.loadByEmail("new@example.com"))
        .thenReturn(Optional.empty());
    when(hasher.hash("password123"))
        .thenReturn("hashed");
    var savedAccount = new Account("generated-id", "Name", "new@example.com", "hashed", false);
    when(addAccountRepository.add(any(Account.class)))
        .thenReturn(savedAccount);

    var params = new AddAccount.Params("Name", "new@example.com", "password123", false);
    var result = sut.execute(params);

    assertNotNull(result);
    assertEquals("new@example.com", result.email());
    verify(addAccountRepository).add(any(Account.class));
  }
}
