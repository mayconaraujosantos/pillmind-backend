package com.pillmind.main.config;

import javax.sql.DataSource;

import org.jdbi.v3.core.Jdbi;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * Configuração do banco de dados PostgreSQL
 */
public class DatabaseConfig {
  private static final String CONNECTION_TEST_QUERY = "SELECT 1";

  private DatabaseConfig() {
    // Utility class
  }

  private static HikariDataSource dataSource;
  private static Jdbi jdbi;

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
    config.setPoolName("pillmind-hikari-pool");
    config.setConnectionTestQuery(CONNECTION_TEST_QUERY);
    config.setInitializationFailTimeout(-1);

    dataSource = new HikariDataSource(config);
    jdbi = Jdbi.create(dataSource);
  }

  public static DataSource getDataSource() {
    return dataSource;
  }

  public static Jdbi getJdbi() {
    return jdbi;
  }

  public static void close() {
    if (dataSource != null) {
      dataSource.close();
    }
  }
}
