package com.pillmind.data.protocols.db;

import java.util.Optional;

import com.pillmind.domain.models.Account;

/**
 * Repositório para carregar conta por ID
 */
public interface LoadAccountByIdRepository {
  Optional<Account> loadById(String id);
}
