package com.pillmind.main.routes;

import java.util.Map;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.openapi.HttpMethod;
import io.javalin.openapi.OpenApi;
import io.javalin.openapi.OpenApiResponse;

/**
 * Rotas de health check
 */
public class HealthRoutes implements Routes {

    @OpenApi(path = "/api/health", methods = HttpMethod.GET, summary = "Health Check", description = "Returns the health status of the API", tags = {
            "Health" }, responses = {
                    @OpenApiResponse(status = "200", description = "API is healthy")
            })
    public void health(Context ctx) {
        ctx.json(Map.of("status", "ok", "timestamp", System.currentTimeMillis()));
    }

    @Override
    public void setup(Javalin app) {
        app.get("/api/health", this::health);
    }
}
