package com.pillmind.data.usecases;

import com.pillmind.data.protocols.cryptography.Encrypter;
import com.pillmind.data.protocols.cryptography.HashComparer;
import com.pillmind.data.protocols.db.LoadAccountByEmailRepository;
import com.pillmind.domain.usecases.Authentication;

/**
 * Implementação do caso de uso Authentication
 */
public class DbAuthentication extends DbUseCase implements Authentication {
  private final LoadAccountByEmailRepository loadAccountByEmailRepository;
  private final HashComparer hashComparer;
  private final Encrypter encrypter;

  public DbAuthentication(
      LoadAccountByEmailRepository loadAccountByEmailRepository,
      HashComparer hashComparer,
      Encrypter encrypter
  ) {
    this.loadAccountByEmailRepository = loadAccountByEmailRepository;
    this.hashComparer = hashComparer;
    this.encrypter = encrypter;
  }

  @Override
  public Result execute(Params params) {
    var account = loadAccountByEmailRepository.loadByEmail(params.email())
        .orElseThrow(() -> new RuntimeException("Invalid credentials"));

    if (account.googleAccount()) {
      throw new RuntimeException("Google accounts must use Google Sign In");
    }

    if (account.password() == null ||
        !hashComparer.compare(params.password(), account.password())) {
      throw new RuntimeException("Invalid credentials");
    }

    var accessToken = encrypter.encrypt(account.id());

    return new Result(accessToken, account.id());
  }
}
