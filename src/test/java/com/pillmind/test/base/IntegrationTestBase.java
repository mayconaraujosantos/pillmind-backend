package com.pillmind.test.base;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import com.pillmind.main.di.ApplicationBootstrap;
import com.pillmind.main.di.Container;
import com.pillmind.test.config.TestDatabaseConfig;

/**
 * Classe base para testes de integração
 * Fornece setup e teardown do banco SQLite (memória compartilhada)
 */
public abstract class IntegrationTestBase {
    protected static Container container;
    protected static Connection connection;

    @BeforeAll
    static void setUpDatabase() throws SQLException {
        connection = TestDatabaseConfig.initializeDatabase();

        // Bootstrap com a mesma conexão SQLite de teste
        try {
            ApplicationBootstrap bootstrap = new ApplicationBootstrap();
            bootstrap.bootstrap(connection);
            container = bootstrap.getContainer();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao inicializar bootstrap", e);
        }
    }

    @BeforeEach
    void cleanDatabase() throws SQLException {
        TestDatabaseConfig.cleanAllTables();
    }

    /**
     * Helper para buscar valor do banco
     */
    protected <T> T queryOne(String sql, Class<T> type) throws SQLException {
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getObject(1, type);
            }
        }
        return null;
    }

    /**
     * Helper para contar registros na tabela users
     */
    protected int countRows(String whereClause) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users";
        if (whereClause != null && !whereClause.isEmpty()) {
            sql += " WHERE " + whereClause;
        }

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    /**
     * Helper para contar registros na tabela local_accounts
     */
    protected int countLocalAccounts(String whereClause) throws SQLException {
        String sql = "SELECT COUNT(*) FROM local_accounts";
        if (whereClause != null && !whereClause.isEmpty()) {
            sql += " WHERE " + whereClause;
        }

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    /**
     * Helper para executar SQL
     */
    protected void executeSql(String sql) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }
}
