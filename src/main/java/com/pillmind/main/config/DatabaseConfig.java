package com.pillmind.main.config;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * Configuração do pool JDBC (SQLite ou outro driver via {@code DATABASE_URL}).
 */
public class DatabaseConfig {
  private DatabaseConfig() {
    // Utility class
  }

  private static final String SQLITE_PRAGMAS = "PRAGMA foreign_keys = ON; PRAGMA journal_mode = WAL;";

  private static HikariDataSource dataSource;

  static {
    HikariConfig config = new HikariConfig();
    config.setJdbcUrl(Env.DATABASE_URL);
    config.setUsername(Env.DATABASE_USER);
    config.setPassword(Env.DATABASE_PASSWORD);
    if (Env.isSqlite()) {
      config.setConnectionInitSql(SQLITE_PRAGMAS);
      // SQLite: um writer por vez; várias conexões aumentam "database is locked"
      config.setMaximumPoolSize(1);
      config.setMinimumIdle(1);
    } else {
      config.setMaximumPoolSize(10);
      config.setMinimumIdle(5);
    }
    config.setConnectionTimeout(30000);
    config.setIdleTimeout(600000);
    config.setMaxLifetime(1800000);

    dataSource = new HikariDataSource(config);
  }

  public static DataSource getDataSource() {
    return dataSource;
  }

  public static Connection getConnection() throws SQLException {
    return dataSource.getConnection();
  }

  public static void close() {
    if (dataSource != null) {
      dataSource.close();
    }
  }
}
