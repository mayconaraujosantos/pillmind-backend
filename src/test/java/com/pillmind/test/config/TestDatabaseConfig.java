package com.pillmind.test.config;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuração do banco H2 em memória para testes
 */
public class TestDatabaseConfig {
    private static final Logger logger = LoggerFactory.getLogger(TestDatabaseConfig.class);

    private static final String H2_URL = "jdbc:h2:mem:pillmind_test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE";
    private static final String H2_USER = "sa";
    private static final String H2_PASSWORD = "";

    private static JdbcConnectionPool dataSource;

    /**
     * Inicializa o banco H2 com migrations e retorna o DataSource
     */
    public static DataSource initializeDatabase() {
        logger.info("Inicializando banco H2 em memória...");

        dataSource = JdbcConnectionPool.create(H2_URL, H2_USER, H2_PASSWORD);
        logger.info("✓ Banco H2 conectado");

        runMigrations();

        return dataSource;
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
     * Limpa dados de uma tabela
     */
    public static void cleanTable(String tableName) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             var stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM " + tableName);
            logger.debug("Tabela '{}' foi limpa", tableName);
        }
    }

    /**
     * Limpa todos os dados das tabelas
     */
    public static void cleanAllTables() throws SQLException {
        logger.debug("Limpando todas as tabelas...");
        cleanTable("oauth_accounts");
        cleanTable("local_accounts");
        cleanTable("users");
    }

    /**
     * Fecha o pool de conexões
     */
    public static void closeDatabase() {
        if (dataSource != null) {
            dataSource.dispose();
            logger.info("Banco H2 fechado");
        }
    }
}
