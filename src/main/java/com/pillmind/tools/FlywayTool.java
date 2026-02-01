package com.pillmind.tools;

import com.pillmind.main.config.FlywayConfig;
import java.util.logging.Logger;

public class FlywayTool {
    private static final Logger LOGGER = Logger.getLogger(FlywayTool.class.getName());

    public static void main(String[] args) {
        String command = (args != null && args.length > 0) ? args[0] : "migrate";

        switch (command.toLowerCase()) {
            case "migrate" -> FlywayConfig.migrate();
            case "clean" -> FlywayConfig.clean();
            case "info" -> FlywayConfig.info();
            case "validate" -> FlywayConfig.validate();
            case "repair" -> FlywayConfig.repair();
            default -> {
                LOGGER.warning(String.format("Unknown command: %s", command));
                LOGGER.warning("Usage: flyway <migrate|clean|info|validate|repair>");
                System.exit(1);
            }
        }
    }
}
