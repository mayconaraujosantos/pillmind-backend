package com.pillmind.infra.db.postgres;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;

/**
 * Classe base para repositórios JDBC (SQLite em produção; nomes históricos "postgres").
 */
public abstract class PostgresRepository {
  protected final Connection connection;

  protected PostgresRepository(Connection connection) {
    this.connection = connection;
  }

  /** SQLite JDBC grava {@code LocalDateTime} via setObject em ISO-8601; use Timestamp para leitura com getTimestamp. */
  protected static void setTimestamp(PreparedStatement stmt, int index, LocalDateTime value) throws SQLException {
    if (value == null) {
      stmt.setNull(index, Types.TIMESTAMP);
    } else {
      stmt.setTimestamp(index, Timestamp.valueOf(value));
    }
  }

  protected void closeConnection() throws SQLException {
    if (connection != null && !connection.isClosed()) {
      connection.close();
    }
  }
}
