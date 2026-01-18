package com.pillmind.data.protocols.db;

import com.pillmind.domain.models.Account;

import java.util.Optional;

/**
 * Protocolo para carregar uma conta por email
 */
public interface LoadAccountByEmailRepository {
  Optional<Account> loadByEmail(String email);
}
