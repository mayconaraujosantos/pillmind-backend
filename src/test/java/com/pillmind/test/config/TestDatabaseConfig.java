package com.pillmind.test.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SQLite em memória compartilhada para testes (mesmo stack que produção dev).
 */
public class TestDatabaseConfig {
    private static final Logger logger = LoggerFactory.getLogger(TestDatabaseConfig.class);

    /**
     * {@code cache=shared} permite Flyway e o código de teste usarem o mesmo DB em memória.
     */
    private static final String SQLITE_URL = "jdbc:sqlite:file:pillmind_test?mode=memory&cache=shared";

    private static Connection connection;

    /**
     * Inicializa o SQLite com migrations Flyway.
     */
    public static Connection initializeDatabase() throws SQLException {
        logger.info("Inicializando SQLite em memória para testes...");

        try {
            Class.forName("org.sqlite.JDBC");
            logger.debug("Driver SQLite carregado");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver SQLite não encontrado no classpath", e);
        }

        connection = DriverManager.getConnection(SQLITE_URL);
        try (var st = connection.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON");
        }
        logger.info("✓ SQLite de teste conectado");

        runMigrations();

        return connection;
    }

    private static void runMigrations() {
        logger.info("Executando migrations Flyway...");

        Flyway flyway = Flyway.configure()
                .dataSource(SQLITE_URL, "", "")
                .initSql("PRAGMA foreign_keys = ON")
                .locations("classpath:db/migration")
                .baselineOnMigrate(true)
                .load();

        flyway.migrate();
        logger.info("✓ Migrations concluídas");
    }

    public static Connection getConnection() {
        if (connection == null) {
            throw new IllegalStateException(
                    "Banco de dados não foi inicializado. Chame initializeDatabase() primeiro.");
        }
        return connection;
    }

    public static void cleanTable(String tableName) throws SQLException {
        try (var stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM " + tableName);
            logger.debug("Tabela '{}' foi limpa", tableName);
        }
    }

    public static void cleanAllTables() throws SQLException {
        logger.debug("Limpando todas as tabelas...");
        cleanTable("medicines");
        cleanTable("oauth_accounts");
        cleanTable("local_accounts");
        cleanTable("users");
    }

    public static void closeDatabase() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            logger.info("SQLite de teste fechado");
        }
    }
}
