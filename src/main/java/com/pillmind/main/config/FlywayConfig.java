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
        // Usa o mesmo DataSource do Hikari para o Flyway não depender de DriverDataSource +
        // Class.forName(org.sqlite.JDBC) (que falha se o IDE/run não tiver o sqlite-jdbc no classpath).
        var config = Flyway.configure()
                .dataSource(DatabaseConfig.getDataSource())
                .locations("classpath:db/migration")
                .baselineOnMigrate(true);
        if (Env.isSqlite()) {
            config.initSql("PRAGMA foreign_keys = ON");
        }
        return config.load();
    }
}
