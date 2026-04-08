package com.pillmind.domain.usecases;

import com.pillmind.domain.models.Account;

/**
 * Caso de uso: Adicionar uma nova conta
 */
public interface AddAccount extends UseCase<AddAccount.Params, Account> {
  record Params(
      String name,
      String email,
      String password,
      boolean googleAccount,
      String googleId,
      String pictureUrl) {
  }
}
