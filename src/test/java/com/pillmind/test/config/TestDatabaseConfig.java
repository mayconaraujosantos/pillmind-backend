package com.pillmind.test.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuração do banco H2 em memória para testes
 * H2 oferece performance excelente e é compatível com PostgreSQL
 */
public class TestDatabaseConfig {
    private static final Logger logger = LoggerFactory.getLogger(TestDatabaseConfig.class);

    private static final String H2_URL = "jdbc:h2:mem:pillmind_test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE";
    private static final String H2_USER = "sa";
    private static final String H2_PASSWORD = "";

    private static Connection connection;

    /**
     * Inicializa o banco H2 com migrations
     */
    public static Connection initializeDatabase() throws SQLException {
        logger.info("Inicializando banco H2 em memória...");

        // Carrega o driver H2 explicitamente
        try {
            Class.forName("org.h2.Driver");
            logger.debug("Driver H2 carregado");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver H2 não encontrado no classpath", e);
        }

        // Cria conexão
        connection = DriverManager.getConnection(H2_URL, H2_USER, H2_PASSWORD);
        logger.info("✓ Banco H2 conectado");

        // Executa migrations
        runMigrations();

        return connection;
    }

    /**
     * Executa migrations Flyway
     */
    private static void runMigrations() {
        logger.info("Executando migrations Flyway...");

        Flyway flyway = Flyway.configure()
                .dataSource(H2_URL, H2_USER, H2_PASSWORD)
                .locations("classpath:db/migration")
                .baselineOnMigrate(true)
                .load();

        flyway.migrate();
        logger.info("✓ Migrations concluídas");
    }

    /**
     * Obtém a conexão existente
     */
    public static Connection getConnection() {
        if (connection == null) {
            throw new IllegalStateException(
                    "Banco de dados não foi inicializado. Chame initializeDatabase() primeiro.");
        }
        return connection;
    }

    /**
     * Limpa dados de uma tabela
     */
    public static void cleanTable(String tableName) throws SQLException {
        try (var stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM " + tableName);
            logger.debug("Tabela '{}' foi limpa", tableName);
        }
    }

    /**
     * Limpa todos os dados das tabelas
     */
    public static void cleanAllTables() throws SQLException {
        logger.debug("Limpando todas as tabelas...");
        cleanTable("accounts");
    }

    /**
     * Fecha a conexão
     */
    public static void closeDatabase() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            logger.info("Banco H2 fechado");
        }
    }
}
