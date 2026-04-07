package com.pillmind.infra.db.postgres;

import org.jdbi.v3.core.Jdbi;

/**
 * Classe base para repositórios PostgreSQL
 */
public abstract class PostgresRepository {
  protected final Jdbi jdbi;

  protected PostgresRepository(Jdbi jdbi) {
    this.jdbi = jdbi;
  }
}
