package com.pillmind;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pillmind.main.config.Env;
import com.pillmind.main.config.FlywayConfig;
import com.pillmind.main.config.SwaggerConfig;
import com.pillmind.main.di.ApplicationBootstrap;
import com.pillmind.main.di.Container;
import com.pillmind.main.routes.AuthRoutes;
import com.pillmind.main.routes.HealthRoutes;
import com.pillmind.main.routes.SwaggerRoutes;
import com.pillmind.presentation.handlers.ErrorHandlers;

import io.javalin.Javalin;
import io.javalin.plugin.bundled.CorsPluginConfig;

/**
 * Ponto de entrada da aplicação
 * Configura e inicia o servidor Javalin
 */
public class Main {
  private static final Logger logger = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) {
    try {
      logger.info("=== Iniciando PillMind Backend ===");
      logger.info("Porta: {}", Env.PORT);
      logger.info("Database: {}", Env.DATABASE_URL);

      // Executa migrations do banco de dados
      logger.info("Executando migrations do Flyway...");
      FlywayConfig.migrate();
      logger.info("Migrations concluídas com sucesso!");

      // Bootstrap das dependências
      logger.info("Executando bootstrap da aplicação...");
      ApplicationBootstrap bootstrap = new ApplicationBootstrap();
      bootstrap.bootstrap();
      Container container = bootstrap.getContainer();
      logger.info("Bootstrap concluído!");

      var app = Javalin
          .create(config -> {
            config.bundledPlugins.enableCors(cors -> cors.addRule(CorsPluginConfig.CorsRule::anyHost));
            // Registra plugins do Swagger/OpenAPI
            config.registerPlugin(SwaggerConfig.createOpenApiPlugin());
            config.registerPlugin(SwaggerConfig.createSwaggerPlugin());
          });

      // Configurar handlers de erro globais
      ErrorHandlers.configure(app);

      // Configura rotas
      logger.info("Configurando rotas...");
      container.resolve("route.health", HealthRoutes.class).setup(app);
      container.resolve("route.auth", AuthRoutes.class).setup(app);
      container.resolve("route.swagger", SwaggerRoutes.class).setup(app);
      logger.info("Rotas configuradas!");

      // Inicia servidor
      logger.info("Iniciando servidor na porta {}...", Env.PORT);
      app.start("0.0.0.0", Env.PORT);
      logger.info("✓ Servidor iniciado com sucesso!");
      logger.info("Acesse: http://localhost:{}/swagger-ui", Env.PORT);

    } catch (Exception e) {
      logger.error("✗ Erro ao iniciar a aplicação:", e);
      System.exit(1);
    }
  }

}
