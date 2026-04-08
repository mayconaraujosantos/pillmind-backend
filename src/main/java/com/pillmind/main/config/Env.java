package com.pillmind.main.config;

import java.io.InputStream;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * Configurações da aplicação via variáveis de ambiente e arquivos de properties
 *
 * Ordem de precedência:
 * 1) Variável de ambiente (ENV)
 * 2) System property (-D)
 * 3) application-<env>.properties
 * 4) application.properties
 * 5) valor padrão
 */
public class Env {
  private static final String DEFAULT_ENV = "dev";
  private static final String JDBC_PREFIX = "jdbc:";
  private static final String JDBC_POSTGRES_PREFIX = "jdbc:postgresql://";
  private static final String POSTGRES_SCHEME_PREFIX = "postgres://";
  private static final String POSTGRESQL_SCHEME_PREFIX = "postgresql://";
  private static final String APP_ENV_INTERNAL = getEnvOrSystem("APP_ENV", DEFAULT_ENV).toLowerCase();
  public static final String APP_ENV = APP_ENV_INTERNAL;

  private static final Properties PROPERTIES = loadProperties(APP_ENV_INTERNAL);

  private Env() {
    // Utility class
  }

  public static final int PORT = Integer.parseInt(
      getEnvOrProperty("PORT", "8182"));

  private static final String DATABASE_URL_RAW = getDatabaseConfigByEnvironment(
      "DATABASE_URL",
      JDBC_POSTGRES_PREFIX + "localhost:5432/pillmind_dev");

  private static final ParsedDatabaseUrl PARSED_DATABASE_URL = parseDatabaseUrl(DATABASE_URL_RAW);

  public static final String DATABASE_URL = PARSED_DATABASE_URL.jdbcUrl();

  public static final String DATABASE_URL_SAFE = sanitizeDatabaseUrl(DATABASE_URL);

  public static final String DATABASE_USER = PARSED_DATABASE_URL.username() != null
      ? PARSED_DATABASE_URL.username()
      : getDatabaseConfigByEnvironment("DATABASE_USER", "postgres");

  public static final String DATABASE_PASSWORD = PARSED_DATABASE_URL.password() != null
      ? PARSED_DATABASE_URL.password()
      : getDatabaseConfigByEnvironment("DATABASE_PASSWORD", "postgres");

  public static final String JWT_SECRET = getEnvOrProperty(
      "JWT_SECRET",
      "pillmind-secret-key-change-in-production-min-256-bits");

  public static final long JWT_EXPIRATION_IN_MS = Long.parseLong(
      getEnvOrProperty("JWT_EXPIRATION_IN_MS", "86400000") // 24 horas
  );

  public static final int BCRYPT_SALT_ROUNDS = Integer.parseInt(
      getEnvOrProperty("BCRYPT_SALT_ROUNDS", "12"));

  public static final String GOOGLE_CLIENT_ID = getEnvOrProperty(
      "GOOGLE_CLIENT_ID",
      "1047433217870-r49q3eau6pq952hsrv8kc0o0f8anie7p.apps.googleusercontent.com");

  public static final String BASE_URL = getEnvOrProperty(
      "BASE_URL",
      "http://localhost:" + PORT);

  public static final String CLOUDFLARE_ACCOUNT_ID = getEnvOrProperty(
      "CLOUDFLARE_ACCOUNT_ID",
      "");

  public static final String CLOUDFLARE_IMAGES_API_TOKEN = getEnvOrProperty(
      "CLOUDFLARE_IMAGES_API_TOKEN",
      "");

  public static final String CLOUDFLARE_IMAGES_DELIVERY_BASE_URL = getEnvOrProperty(
      "CLOUDFLARE_IMAGES_DELIVERY_BASE_URL",
      "");

  // MinIO Configuration
  public static final String MINIO_ENDPOINT = getEnvOrProperty(
      "MINIO_ENDPOINT",
      "http://localhost:9000");

  public static final String MINIO_ACCESS_KEY = getEnvOrProperty(
      "MINIO_ACCESS_KEY",
      "minioadmin");

  public static final String MINIO_SECRET_KEY = getEnvOrProperty(
      "MINIO_SECRET_KEY",
      "minioadmin");

  public static final String MINIO_BUCKET = getEnvOrProperty(
      "MINIO_BUCKET",
      "pillmind-images");

  // Image Storage Provider: "minio" or "cloudflare"
  public static final String IMAGE_STORAGE_PROVIDER = getEnvOrProperty(
      "IMAGE_STORAGE_PROVIDER",
      "minio");

  private static String getEnvOrProperty(String key, String defaultValue) {
    String envValue = System.getenv(key);
    if (envValue != null && !envValue.isBlank()) {
      return envValue;
    }

    String sysValue = System.getProperty(key);
    if (sysValue != null && !sysValue.isBlank()) {
      return sysValue;
    }

    String propValue = PROPERTIES.getProperty(key);
    if (propValue != null && !propValue.isBlank()) {
      return propValue;
    }

    return defaultValue;
  }

  private static String getEnvOrSystem(String key, String defaultValue) {
    String envValue = System.getenv(key);
    if (envValue != null && !envValue.isBlank()) {
      return envValue;
    }

    String sysValue = System.getProperty(key);
    if (sysValue != null && !sysValue.isBlank()) {
      return sysValue;
    }

    return defaultValue;
  }

  private static Properties loadProperties(String env) {
    Properties props = new Properties();
    loadClasspathProperties(props, "application.properties");
    loadClasspathProperties(props, String.format("application-%s.properties", env));
    return props;
  }

  private static void loadClasspathProperties(Properties props, String resourceName) {
    try (InputStream in = Env.class.getClassLoader().getResourceAsStream(resourceName)) {
      if (in != null) {
        props.load(in);
      }
    } catch (Exception ignored) {
      // Falhar silenciosamente para manter compatibilidade
    }
  }

  private static String getDatabaseConfigByEnvironment(String key, String defaultValue) {
    String envKeyByProfile = key + "_" + APP_ENV_INTERNAL.toUpperCase();

    String envProfileValue = getEnvOrSystem(envKeyByProfile, null);
    if (envProfileValue != null && !envProfileValue.isBlank()) {
      return envProfileValue;
    }

    String profilePropertyValue = PROPERTIES.getProperty(key);
    if (profilePropertyValue != null && !profilePropertyValue.isBlank()) {
      return profilePropertyValue;
    }

    String legacyValue = getEnvOrSystem(key, null);
    if (legacyValue != null && !legacyValue.isBlank()) {
      return legacyValue;
    }

    return defaultValue;
  }

  static ParsedDatabaseUrl parseDatabaseUrl(String rawValue) {
    if (rawValue == null || rawValue.isBlank()) {
      return new ParsedDatabaseUrl(JDBC_POSTGRES_PREFIX + "localhost:5432/pillmind_dev", null, null);
    }

    String value = rawValue.trim();
    String uriValue = normalizeToUriValue(value);
    if (uriValue == null) {
      return new ParsedDatabaseUrl(value, null, null);
    }

    try {
      URI uri = URI.create(uriValue);
      String jdbcUrl = buildJdbcUrl(uri);
      if (jdbcUrl == null) {
        return new ParsedDatabaseUrl(value, null, null);
      }

      Credentials credentials = parseCredentials(uri.getUserInfo());
      return new ParsedDatabaseUrl(jdbcUrl, credentials.username(), credentials.password());
    } catch (IllegalArgumentException ex) {
      return new ParsedDatabaseUrl(value, null, null);
    }
  }

  static String sanitizeDatabaseUrl(String databaseUrl) {
    if (databaseUrl == null || databaseUrl.isBlank()) {
      return databaseUrl;
    }

    String value = databaseUrl.trim();
    if (!value.startsWith(JDBC_POSTGRES_PREFIX)) {
      return value;
    }

    String uriValue = value.substring(JDBC_PREFIX.length());
    try {
      URI uri = URI.create(uriValue);
      String safeUrl = buildJdbcUrl(uri);
      if (safeUrl == null) {
        return value;
      }
      return safeUrl;
    } catch (IllegalArgumentException ex) {
      return value;
    }
  }

  private static String normalizeToUriValue(String value) {
    if (value.startsWith(JDBC_POSTGRES_PREFIX)) {
      return value.substring(JDBC_PREFIX.length());
    }

    if (value.startsWith(POSTGRES_SCHEME_PREFIX)) {
      return POSTGRESQL_SCHEME_PREFIX + value.substring(POSTGRES_SCHEME_PREFIX.length());
    }

    if (value.startsWith(POSTGRESQL_SCHEME_PREFIX)) {
      return value;
    }

    return null;
  }

  private static String buildJdbcUrl(URI uri) {
    String host = uri.getHost();
    if (host == null || host.isBlank()) {
      return null;
    }

    StringBuilder jdbcUrl = new StringBuilder(JDBC_POSTGRES_PREFIX).append(host);

    if (uri.getPort() > -1) {
      jdbcUrl.append(':').append(uri.getPort());
    }

    String path = uri.getPath();
    jdbcUrl.append((path == null || path.isBlank()) ? "/" : path);

    if (uri.getQuery() != null && !uri.getQuery().isBlank()) {
      jdbcUrl.append('?').append(uri.getQuery());
    }

    return jdbcUrl.toString();
  }

  private static Credentials parseCredentials(String userInfo) {
    if (userInfo == null || userInfo.isBlank()) {
      return new Credentials(null, null);
    }

    String[] parts = userInfo.split(":", 2);
    String username = decode(parts[0]);
    String password = parts.length > 1 ? decode(parts[1]) : null;
    return new Credentials(username, password);
  }

  private static String decode(String value) {
    return URLDecoder.decode(value, StandardCharsets.UTF_8);
  }

  static record ParsedDatabaseUrl(String jdbcUrl, String username, String password) {
  }

  private static record Credentials(String username, String password) {
  }
}
