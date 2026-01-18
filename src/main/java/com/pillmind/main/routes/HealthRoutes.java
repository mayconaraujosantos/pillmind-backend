package com.pillmind.main.routes;

import io.javalin.Javalin;

import java.util.Map;

/**
 * Rotas de health check
 */
public class HealthRoutes implements Routes {
    @Override
    public void setup(Javalin app) {
        app.get("/api/health", ctx -> {
            ctx.json(Map.of("status", "ok", "timestamp", System.currentTimeMillis()));
        });
    }
}
