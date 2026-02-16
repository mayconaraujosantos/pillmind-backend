package com.pillmind.main.config;

import java.io.InputStream;
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
  private static final String APP_ENV_INTERNAL = getEnvOrSystem("APP_ENV", DEFAULT_ENV).toLowerCase();
  public static final String APP_ENV = APP_ENV_INTERNAL;

  private static final Properties PROPERTIES = loadProperties(APP_ENV_INTERNAL);

  private Env() {
    // Utility class
  }

  public static final int PORT = Integer.parseInt(
    getEnvOrProperty("PORT", "8080"));

  public static final String DATABASE_URL = getEnvOrProperty(
    "DATABASE_URL",
    "jdbc:postgresql://localhost:5432/pillmind");

  public static final String DATABASE_USER = getEnvOrProperty(
    "DATABASE_USER",
    "postgres");

  public static final String DATABASE_PASSWORD = getEnvOrProperty(
    "DATABASE_PASSWORD",
    "postgres");

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
}
