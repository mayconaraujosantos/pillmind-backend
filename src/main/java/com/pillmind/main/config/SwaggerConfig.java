package com.pillmind.main.config;

import io.javalin.openapi.plugin.OpenApiPlugin;
import io.javalin.openapi.plugin.swagger.SwaggerPlugin;

/**
 * Configuração do Swagger/OpenAPI para documentação automática da API
 */
public class SwaggerConfig {

    private SwaggerConfig() {
        // Utility class
    }

    /**
     * Cria e configura o plugin OpenAPI
     */
    public static OpenApiPlugin createOpenApiPlugin() {
        return new OpenApiPlugin(config -> {
            config.withDefinitionConfiguration((version, definition) -> definition
                    .withOpenApiInfo(info -> {
                        info.setTitle("PillMind API");
                        info.setVersion("1.0.0");
                        info.setDescription("API for PillMind - Medication Management System");
                    })
                    .withServer(server -> {
                        server.setUrl("http://localhost:" + Env.PORT);
                        server.setDescription("Local Development");
                    })
                    .withServer(server -> {
                        server.setUrl("https://pillmind.192.168.1.7.nip.io");
                        server.setDescription("Development Server");
                    }));
            config.withDocumentationPath("/openapi");
        });
    }

    /**
     * Cria e configura o plugin Swagger UI
     */
    public static SwaggerPlugin createSwaggerPlugin() {
        return new SwaggerPlugin(config -> {
            config.setDocumentationPath("/openapi");
        });
    }
}
