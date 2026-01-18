package com.pillmind.domain.usecases;

/**
 * Caso de uso: Autenticar uma conta
 */
public interface Authentication extends UseCase<Authentication.Params, Authentication.Result> {
  record Params(String email, String password) {}

  record Result(String accessToken, String accountId) {}
}
