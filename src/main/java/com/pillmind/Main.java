package com.pillmind;

import com.pillmind.main.config.Env;
import com.pillmind.main.config.FlywayConfig;
import com.pillmind.main.routes.AuthRoutes;
import com.pillmind.main.routes.HealthRoutes;
import com.pillmind.main.routes.SwaggerRoutes;

import io.javalin.Javalin;
import io.javalin.plugin.bundled.CorsPluginConfig;

/**
 * Ponto de entrada da aplicação
 * Configura e inicia o servidor Javalin
 */
public class Main {
  public static void main(String[] args) {
    // Executa migrations do banco de dados
    FlywayConfig.migrate();

    var app = Javalin
        .create(config -> {
          config.bundledPlugins.enableCors(cors -> cors.addRule(CorsPluginConfig.CorsRule::anyHost));
        });

    // Configura rotas
    new SwaggerRoutes().setup(app);
    new HealthRoutes().setup(app);
    new AuthRoutes().setup(app);

    // Inicia servidor
    app.start(Env.PORT);
  }
}
