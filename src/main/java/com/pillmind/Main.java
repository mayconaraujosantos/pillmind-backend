package com.pillmind;

import com.pillmind.main.config.Env;
import com.pillmind.main.routes.AuthRoutes;
import com.pillmind.main.routes.HealthRoutes;

import io.javalin.Javalin;
import io.javalin.plugin.bundled.CorsPluginConfig;

/**
 * Ponto de entrada da aplicação
 * Configura e inicia o servidor Javalin
 */
public class Main {
  public static void main(String[] args) {
    var app = Javalin
        .create(config -> config.bundledPlugins.enableCors(cors -> cors.addRule(CorsPluginConfig.CorsRule::anyHost)));

    // Configura rotas
    new HealthRoutes().setup(app);
    new AuthRoutes().setup(app);

    // Inicia servidor
    app.start(Env.PORT);
  }
}
