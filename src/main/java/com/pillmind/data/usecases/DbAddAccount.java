package com.pillmind.data.usecases;

import com.pillmind.data.protocols.cryptography.Hasher;
import com.pillmind.data.protocols.db.AddAccountRepository;
import com.pillmind.data.protocols.db.LoadAccountByEmailRepository;
import com.pillmind.domain.models.Account;
import com.pillmind.domain.usecases.AddAccount;

import java.util.UUID;

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
      LoadAccountByEmailRepository loadAccountByEmailRepository
  ) {
    this.hasher = hasher;
    this.addAccountRepository = addAccountRepository;
    this.loadAccountByEmailRepository = loadAccountByEmailRepository;
  }

  @Override
  public Account execute(Params params) {
    var existingAccount = loadAccountByEmailRepository.loadByEmail(params.email());
    if (existingAccount.isPresent()) {
      throw new RuntimeException("Email already exists");
    }

    var hashedPassword = params.googleAccount() 
        ? null 
        : hasher.hash(params.password());

    var account = new Account(
        UUID.randomUUID().toString(),
        params.name(),
        params.email(),
        hashedPassword,
        params.googleAccount()
    );

    return addAccountRepository.add(account);
  }
}
