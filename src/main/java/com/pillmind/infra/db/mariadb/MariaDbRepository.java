package com.pillmind.infra.db.mariadb;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Classe base para repositórios MariaDB
 */
public abstract class MariaDbRepository {
  protected final Connection connection;

  protected MariaDbRepository(Connection connection) {
    this.connection = connection;
  }

  protected void closeConnection() throws SQLException {
    if (connection != null && !connection.isClosed()) {
      connection.close();
    }
  }
}
