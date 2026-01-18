package com.pillmind.infra.db.postgres;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Classe base para repositórios PostgreSQL
 */
public abstract class PostgresRepository {
  protected final Connection connection;

  protected PostgresRepository(Connection connection) {
    this.connection = connection;
  }

  protected void closeConnection() throws SQLException {
    if (connection != null && !connection.isClosed()) {
      connection.close();
    }
  }
}
