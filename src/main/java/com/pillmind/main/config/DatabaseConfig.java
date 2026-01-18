package com.pillmind.main.config;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * Configuração do banco de dados PostgreSQL
 */
public class DatabaseConfig {
  private DatabaseConfig() {
    // Utility class
  }

  private static HikariDataSource dataSource;

  static {
    HikariConfig config = new HikariConfig();
    config.setJdbcUrl(Env.DATABASE_URL);
    config.setUsername(Env.DATABASE_USER);
    config.setPassword(Env.DATABASE_PASSWORD);
    config.setMaximumPoolSize(10);
    config.setMinimumIdle(5);
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
