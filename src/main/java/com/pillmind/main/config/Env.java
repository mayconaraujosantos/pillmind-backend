package com.pillmind.main.config;

/**
 * Configurações da aplicação via variáveis de ambiente
 */
public class Env {
    private Env() {
        // Utility class
    }

    public static final int PORT = Integer.parseInt(
            System.getenv().getOrDefault("PORT", "8080"));

    public static final String DATABASE_URL = System.getenv().getOrDefault(
            "DATABASE_URL",
            "jdbc:postgresql://localhost:5432/pillmind");

    public static final String DATABASE_USER = System.getenv().getOrDefault(
            "DATABASE_USER",
            "postgres");

    public static final String DATABASE_PASSWORD = System.getenv().getOrDefault(
            "DATABASE_PASSWORD",
            "postgres");

    public static final String JWT_SECRET = System.getenv().getOrDefault(
            "JWT_SECRET",
            "pillmind-secret-key-change-in-production-min-256-bits");

    public static final long JWT_EXPIRATION_IN_MS = Long.parseLong(
            System.getenv().getOrDefault("JWT_EXPIRATION_IN_MS", "86400000") // 24 horas
    );

    public static final int BCRYPT_SALT_ROUNDS = Integer.parseInt(
            System.getenv().getOrDefault("BCRYPT_SALT_ROUNDS", "12"));

    public static final String GOOGLE_CLIENT_ID = System.getenv().getOrDefault(
            "GOOGLE_CLIENT_ID",
            "1047433217870-r49q3eau6pq952hsrv8kc0o0f8anie7p.apps.googleusercontent.com");
}
