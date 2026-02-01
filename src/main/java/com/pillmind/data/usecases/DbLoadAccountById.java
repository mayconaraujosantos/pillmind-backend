package com.pillmind.data.usecases;

import com.pillmind.data.protocols.db.LoadAccountByIdRepository;
import com.pillmind.domain.errors.NotFoundException;
import com.pillmind.domain.models.Account;
import com.pillmind.domain.usecases.LoadAccountById;

/**
 * Implementação do caso de uso LoadAccountById
 */
public class DbLoadAccountById extends DbUseCase implements LoadAccountById {
  private final LoadAccountByIdRepository loadAccountByIdRepository;

  public DbLoadAccountById(LoadAccountByIdRepository loadAccountByIdRepository) {
    this.loadAccountByIdRepository = loadAccountByIdRepository;
  }

  @Override
  public Account execute(Params params) {
    return loadAccountByIdRepository.loadById(params.accountId())
        .orElseThrow(() -> new NotFoundException("Conta não encontrada"));
  }
}
