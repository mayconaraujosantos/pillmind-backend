package com.pillmind.main.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class EnvTest {

    @Test
    void shouldNormalizePostgresUrlWithCredentials() {
        String raw = "postgresql://postgres:secret@postgres.railway.internal:5432/railway?sslmode=require";

        Env.ParsedDatabaseUrl parsed = Env.parseDatabaseUrl(raw);

        assertEquals("jdbc:postgresql://postgres.railway.internal:5432/railway?sslmode=require", parsed.jdbcUrl());
        assertEquals("postgres", parsed.username());
        assertEquals("secret", parsed.password());
    }

    @Test
    void shouldNormalizeJdbcUrlWhenCredentialsAreEmbeddedInAuthority() {
        String raw = "jdbc:postgresql://postgres:secret@postgres.railway.internal:5432/railway";

        Env.ParsedDatabaseUrl parsed = Env.parseDatabaseUrl(raw);

        assertEquals("jdbc:postgresql://postgres.railway.internal:5432/railway", parsed.jdbcUrl());
        assertEquals("postgres", parsed.username());
        assertEquals("secret", parsed.password());
    }

    @Test
    void shouldKeepJdbcUrlWithoutCredentials() {
        String raw = "jdbc:postgresql://localhost:5432/pillmind_dev";

        Env.ParsedDatabaseUrl parsed = Env.parseDatabaseUrl(raw);

        assertEquals(raw, parsed.jdbcUrl());
        assertNull(parsed.username());
        assertNull(parsed.password());
    }

    @Test
    void shouldSanitizeJdbcUrlForLogs() {
        String url = "jdbc:postgresql://postgres:secret@postgres.railway.internal:5432/railway?sslmode=require";

        String sanitized = Env.sanitizeDatabaseUrl(url);

        assertEquals("jdbc:postgresql://postgres.railway.internal:5432/railway?sslmode=require", sanitized);
    }

    @Test
    void shouldUseProfileSpecificValueWhenProvided() {
        System.setProperty("DATABASE_URL_DEV", "jdbc:postgresql://localhost:5432/dev_db");
        try {
            String value = invokeGetDatabaseConfigByEnvironment("DATABASE_URL", "fallback");
            assertEquals("jdbc:postgresql://localhost:5432/dev_db", value);
        } finally {
            System.clearProperty("DATABASE_URL_DEV");
        }
    }

    @Test
    void shouldFallbackToLegacyGenericProperty() {
        System.setProperty("CUSTOM_DB_KEY", "legacy-user");
        try {
            String value = invokeGetDatabaseConfigByEnvironment("CUSTOM_DB_KEY", "fallback-user");
            assertEquals("legacy-user", value);
        } finally {
            System.clearProperty("CUSTOM_DB_KEY");
        }
    }

    private String invokeGetDatabaseConfigByEnvironment(String key, String defaultValue) {
        try {
            var method = Env.class.getDeclaredMethod("getDatabaseConfigByEnvironment", String.class, String.class);
            method.setAccessible(true);
            return (String) method.invoke(null, key, defaultValue);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }
}
