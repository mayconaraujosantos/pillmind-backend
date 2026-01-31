package com.pillmind.main.config;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlywayConfig {
    private static final Logger log = LoggerFactory.getLogger(FlywayConfig.class);

    private FlywayConfig() {
    }

    public static void migrate() {
        Flyway flyway = buildFlyway();
        flyway.migrate();
    }

    public static void clean() {
        Flyway flyway = buildFlyway();
        flyway.clean();
    }

    public static void info() {
        Flyway flyway = buildFlyway();
        var info = flyway.info();
        if (info.current() == null) {
            log.info("No migrations applied yet.");
        } else {
            log.info("Current version: {}", info.current().getVersion());
        }
        log.info("Applied: {}", info.applied().length);
        log.info("Pending: {}", info.pending().length);
    }

    public static void validate() {
        Flyway flyway = buildFlyway();
        flyway.validate();
    }

    public static void repair() {
        Flyway flyway = buildFlyway();
        flyway.repair();
    }

    private static Flyway buildFlyway() {
        return Flyway.configure()
                .dataSource(
                        Env.DATABASE_URL,
                        Env.DATABASE_USER,
                        Env.DATABASE_PASSWORD)
                .locations("classpath:db/migration")
                .baselineOnMigrate(true)
                .load();
    }
}
