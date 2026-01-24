package com.pillmind.data.usecases;

import com.pillmind.data.protocols.cryptography.Encrypter;
import com.pillmind.data.protocols.cryptography.HashComparer;
import com.pillmind.data.protocols.db.LoadAccountByEmailRepository;
import com.pillmind.domain.errors.UnauthorizedException;
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
      Encrypter encrypter) {
    this.loadAccountByEmailRepository = loadAccountByEmailRepository;
    this.hashComparer = hashComparer;
    this.encrypter = encrypter;
  }

  @Override
  public Result execute(Params params) {
    var account = loadAccountByEmailRepository.loadByEmail(params.email())
        .orElseThrow(() -> new UnauthorizedException("Email ou senha inválidos"));

    // Se for conta Google, valida de forma diferente
    if (account.googleAccount()) {
      // Se o password for null, é um login OAuth2 válido
      if (params.password() == null) {
        var accessToken = encrypter.encrypt(account.id());
        return new Result(accessToken, account.id());
      }
      // Se enviou senha, mas é conta Google, rejeita
      throw new UnauthorizedException("Esta conta usa login do Google. Use 'Entrar com Google'");
    }

    // Conta tradicional - valida senha
    if (params.password() == null ||
        account.password() == null ||
        !hashComparer.compare(params.password(), account.password())) {
      throw new UnauthorizedException("Email ou senha inválidos");
    }

    var accessToken = encrypter.encrypt(account.id());

    return new Result(accessToken, account.id());
  }
}
