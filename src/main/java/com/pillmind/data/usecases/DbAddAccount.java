package com.pillmind.data.usecases;

import java.time.LocalDateTime;
import java.util.UUID;

import com.pillmind.data.protocols.cryptography.Hasher;
import com.pillmind.data.protocols.db.AddAccountRepository;
import com.pillmind.data.protocols.db.LoadAccountByEmailRepository;
import com.pillmind.domain.errors.ConflictException;
import com.pillmind.domain.models.Account;
import com.pillmind.domain.usecases.AddAccount;

/**
 * Implementação do caso de uso AddAccount
 */
public class DbAddAccount extends DbUseCase implements AddAccount {
  private final Hasher hasher;
  private final AddAccountRepository addAccountRepository;
  private final LoadAccountByEmailRepository loadAccountByEmailRepository;

  public DbAddAccount(
      Hasher hasher,
      AddAccountRepository addAccountRepository,
      LoadAccountByEmailRepository loadAccountByEmailRepository) {
    this.hasher = hasher;
    this.addAccountRepository = addAccountRepository;
    this.loadAccountByEmailRepository = loadAccountByEmailRepository;
  }

  @Override
  public Account execute(Params params) {
    var existingAccount = loadAccountByEmailRepository.loadByEmail(params.email());
    if (existingAccount.isPresent()) {
      var account = existingAccount.get();

      // Caso a conta já exista e seja Google, atualiza dados e lastLoginAt
      if (params.googleAccount() && account.googleAccount()) {
        var updatedAccount = account.withGoogleData(
            params.name(),
            params.googleId(),
            params.pictureUrl(),
            LocalDateTime.now());

        return addAccountRepository.update(updatedAccount);
      }

      // Conta tradicional existente: mantém comportamento atual (erro)
      throw new ConflictException("Este email já está cadastrado. Use outro email ou faça login.");
    }

    var hashedPassword = params.googleAccount()
        ? null
        : hasher.hash(params.password());

    var account = new Account(
        UUID.randomUUID().toString(),
        params.name(),
        params.email(),
        hashedPassword,
        params.googleAccount(),
        params.googleId(),
        params.pictureUrl(),
        params.googleAccount() ? LocalDateTime.now() : null);

    return addAccountRepository.add(account);
  }
}
