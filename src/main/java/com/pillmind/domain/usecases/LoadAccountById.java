package com.pillmind.domain.usecases;

import com.pillmind.domain.models.Account;

/**
 * Caso de uso: Carregar conta por ID
 */
public interface LoadAccountById extends UseCase<LoadAccountById.Params, Account> {
  record Params(String accountId) {
  }
}
