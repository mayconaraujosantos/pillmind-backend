package com.pillmind.data.protocols.db;

import com.pillmind.domain.models.Account;

/**
 * Protocolo para adicionar uma conta ao repositório
 */
public interface AddAccountRepository {
  Account add(Account account);

  Account update(Account account);
}
