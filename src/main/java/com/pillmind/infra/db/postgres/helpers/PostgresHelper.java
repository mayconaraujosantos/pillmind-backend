package com.pillmind.infra.db.postgres.helpers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Helper para operações comuns no PostgreSQL
 */
public class PostgresHelper {
  private PostgresHelper() {
    // Utility class
  }

  public static void close(Connection connection) {
    if (connection != null) {
      try {
        connection.close();
      } catch (SQLException e) {
        // Log error
      }
    }
  }

  public static void close(PreparedStatement statement) {
    if (statement != null) {
      try {
        statement.close();
      } catch (SQLException e) {
        // Log error
      }
    }
  }

  public static void close(ResultSet resultSet) {
    if (resultSet != null) {
      try {
        resultSet.close();
      } catch (SQLException e) {
        // Log error
      }
    }
  }
}
