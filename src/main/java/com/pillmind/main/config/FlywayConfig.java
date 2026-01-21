package com.pillmind.main.config;

import org.flywaydb.core.Flyway;

public class FlywayConfig {
    public static void migrate() {
        Flyway flyway = Flyway.configure()
            .dataSource(
                Env.DATABASE_URL,
                Env.DATABASE_USER,
                Env.DATABASE_PASSWORD
            )
            .locations("classpath:db/migration")
            .load();
        
        flyway.migrate();
    }
}
