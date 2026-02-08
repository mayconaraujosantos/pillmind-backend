package com.pillmind.main.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

/**
 * Loads key/value pairs from a .env file into System properties.
 */
public final class DotenvLoader {
    private DotenvLoader() {
        // Utility class
    }

    public static void load(String filePath, Logger logger) {
        Path path = Path.of(filePath);
        if (!Files.exists(path)) {
            return;
        }

        List<String> loadedKeys = new ArrayList<>();

        try {
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            for (String line : lines) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }

                int idx = trimmed.indexOf('=');
                if (idx <= 0) {
                    continue;
                }

                String key = trimmed.substring(0, idx).trim();
                String value = trimmed.substring(idx + 1).trim();
                value = stripQuotes(value);

                if (key.isEmpty()) {
                    continue;
                }

                if (System.getProperty(key) != null || System.getenv(key) != null) {
                    continue;
                }

                System.setProperty(key, value);
                loadedKeys.add(key);
            }
        } catch (IOException e) {
            logger.warn("Failed to load .env file at {}", filePath, e);
            return;
        }

        if (!loadedKeys.isEmpty()) {
            logger.info("Loaded {} entries from {}", loadedKeys.size(), filePath);
        }
    }

    private static String stripQuotes(String value) {
        if (value.length() >= 2) {
            char first = value.charAt(0);
            char last = value.charAt(value.length() - 1);
            if ((first == '"' && last == '"') || (first == '\'' && last == '\'')) {
                return value.substring(1, value.length() - 1);
            }
        }
        return value;
    }
}
