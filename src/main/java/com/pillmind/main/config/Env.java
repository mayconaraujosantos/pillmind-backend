package com.pillmind.main.config;

import java.util.ArrayList;
import java.util.List;

/**
 * Configurações da aplicação via variáveis de ambiente
 */
public final class Env {
        private Env() {
                // Utility class
        }

        public static final String APP_ENV = getOptional("APP_ENV", "dev");
        private static final boolean IS_PROD = isProd(APP_ENV);

        public static final int PORT = getInt("PORT", 7000, 1, 65535);

        public static final String DATABASE_URL = resolveDatabaseUrl();
        public static final String DATABASE_USER = resolveDatabaseUser();
        public static final String DATABASE_PASSWORD = resolveDatabasePassword();

        private static final String DEV_JWT_SECRET = "dev-only-secret-change";
        public static final String JWT_SECRET = getRequired("JWT_SECRET", DEV_JWT_SECRET);
        public static final long JWT_EXPIRATION_IN_MS = getLong("JWT_EXPIRATION_IN_MS", 86400000L, 1, Long.MAX_VALUE);
        public static final int BCRYPT_SALT_ROUNDS = getInt("BCRYPT_SALT_ROUNDS", 12, 4, 20);

        public static final String GOOGLE_CLIENT_ID = getOptional("GOOGLE_CLIENT_ID", "");
        public static final String GOOGLE_CLIENT_SECRET = getOptional("GOOGLE_CLIENT_SECRET", "");
        public static final String APP_URL = getOptional("APP_URL", "http://localhost:7000");

        public static void validate() {
                if (!IS_PROD) {
                        return;
                }

                List<String> missing = new ArrayList<>();

                if (isBlank(DATABASE_URL)) missing.add("DATABASE_URL");
                if (isBlank(DATABASE_USER)) missing.add("DATABASE_USER");
                if (isBlank(DATABASE_PASSWORD)) missing.add("DATABASE_PASSWORD");
                if (isBlank(JWT_SECRET)) missing.add("JWT_SECRET");

                if (!missing.isEmpty()) {
                        throw new IllegalStateException("Missing required env vars: " + String.join(", ", missing));
                }

                if (DEV_JWT_SECRET.equals(JWT_SECRET)) {
                        throw new IllegalStateException("JWT_SECRET must be set in production.");
                }
        }

        public static void logSummary(org.slf4j.Logger logger) {
                logger.info("Env: {}", APP_ENV);
                logger.info("Port: {}", PORT);
                logger.info("Database: {}", DATABASE_URL);
                logger.info("Database url set: {}", !isBlank(getRaw("DATABASE_URL")));
                logger.info("Db host set: {}", !isBlank(getRaw("DB_HOST")));
                logger.info("Db user set: {}", !isBlank(getRaw("DATABASE_USER")) || !isBlank(getRaw("DB_USER")));
                logger.info("Db password set: {}", !isBlank(getRaw("DATABASE_PASSWORD")) || !isBlank(getRaw("DB_PASSWORD")));
                logger.info("Jwt expiration ms: {}", JWT_EXPIRATION_IN_MS);
                logger.info("Bcrypt rounds: {}", BCRYPT_SALT_ROUNDS);
                logger.info("Google client id set: {}", !isBlank(GOOGLE_CLIENT_ID));
                logger.info("Google client secret set: {}", !isBlank(GOOGLE_CLIENT_SECRET));
                logger.info("App url: {}", APP_URL);
        }

        private static String resolveDatabaseUrl() {
                String rawUrl = getRaw("DATABASE_URL");
                if (!isBlank(rawUrl)) {
                        return normalizeJdbcUrl(rawUrl);
                }

                String host = getRaw("DB_HOST");
                String port = getRaw("DB_PORT");
                String dbName = firstNonBlank(
                                getRaw("DB_NAME"),
                                getRaw("DB_DATABASE"),
                                getRaw("PGDATABASE"),
                                "railway");

                if (!isBlank(host)) {
                        String resolvedPort = isBlank(port) ? "5432" : port.trim();
                        return "jdbc:postgresql://" + host.trim() + ":" + resolvedPort + "/" + dbName.trim();
                }

                return IS_PROD ? "" : "jdbc:postgresql://localhost:5432/pillmind";
        }

        private static String resolveDatabaseUser() {
                String user = firstNonBlank(
                                getRaw("DATABASE_USER"),
                                getRaw("DB_USER"),
                                getRaw("PGUSER"));
                if (!isBlank(user)) {
                        return user;
                }
                return IS_PROD ? "" : "postgres";
        }

        private static String resolveDatabasePassword() {
                String password = firstNonBlank(
                                getRaw("DATABASE_PASSWORD"),
                                getRaw("DB_PASSWORD"),
                                getRaw("PGPASSWORD"));
                if (!isBlank(password)) {
                        return password;
                }
                return IS_PROD ? "" : "postgres";
        }

        private static String normalizeJdbcUrl(String url) {
                String trimmed = url.trim();
                if (trimmed.startsWith("jdbc:")) {
                        return trimmed;
                }
                if (trimmed.startsWith("postgresql://")) {
                        return "jdbc:" + trimmed;
                }
                return trimmed;
        }

        private static String firstNonBlank(String... values) {
                for (String value : values) {
                        if (!isBlank(value)) {
                                return value.trim();
                        }
                }
                return null;
        }

        private static String getRequired(String key, String devDefault) {
                String value = getRaw(key);
                if (value != null) {
                        return value;
                }
                return IS_PROD ? "" : devDefault;
        }

        private static String getOptional(String key, String defaultValue) {
                String value = getRaw(key);
                return value == null ? defaultValue : value;
        }

        private static int getInt(String key, int defaultValue, int min, int max) {
                String raw = getRaw(key);
                int value = raw == null ? defaultValue : Integer.parseInt(raw);
                if (value < min || value > max) {
                        throw new IllegalArgumentException(key + " must be between " + min + " and " + max);
                }
                return value;
        }

        private static long getLong(String key, long defaultValue, long min, long max) {
                String raw = getRaw(key);
                long value = raw == null ? defaultValue : Long.parseLong(raw);
                if (value < min || value > max) {
                        throw new IllegalArgumentException(key + " must be between " + min + " and " + max);
                }
                return value;
        }

        private static String getRaw(String key) {
                String sys = System.getProperty(key);
                if (!isBlank(sys)) {
                        return sys;
                }
                String env = System.getenv(key);
                return isBlank(env) ? null : env;
        }

        private static boolean isProd(String value) {
                return "prod".equalsIgnoreCase(value) || "production".equalsIgnoreCase(value);
        }

        private static boolean isBlank(String value) {
                return value == null || value.trim().isEmpty();
        }
}
