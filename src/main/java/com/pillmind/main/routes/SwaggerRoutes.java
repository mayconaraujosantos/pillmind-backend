package com.pillmind.main.routes;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import io.javalin.Javalin;

public class SwaggerRoutes {
    public void setup(Javalin app) {
        // Serve OpenAPI JSON
        app.get("/api/openapi.json", ctx -> {
            InputStream is = getClass().getClassLoader().getResourceAsStream("openapi.json");
            if (is != null) {
                String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                ctx.contentType("application/json").result(content);
            } else {
                ctx.status(404).result("OpenAPI spec not found");
            }
        });

        // Serve Swagger UI HTML
        app.get("/swagger-ui", ctx -> {
            InputStream is = getClass().getClassLoader().getResourceAsStream("swagger-ui.html");
            if (is != null) {
                String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                ctx.contentType("text/html").result(content);
            } else {
                ctx.status(404).result("Swagger UI not found");
            }
        });

        // Redirect root to Swagger UI
        app.get("/", ctx -> ctx.redirect("/swagger-ui"));
    }
}
