package com.pillmind;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pillmind.main.config.Env;
import com.pillmind.main.config.FlywayConfig;
import com.pillmind.main.di.ApplicationBootstrap;
import com.pillmind.main.di.Container;
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
          });

      // Configurar handlers de erro globais
      configureErrorHandlers(app);

      // Configura rotas
      logger.info("Configurando rotas...");
      container.resolve("route.swagger", SwaggerRoutes.class).setup(app);
      container.resolve("route.health", HealthRoutes.class).setup(app);
      container.resolve("route.auth", AuthRoutes.class).setup(app);
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

  /**
   * Configura handlers de erro globais para capturar e logar exceções
   */
  private static void configureErrorHandlers(Javalin app) {
    // Handler para exceções não capturadas
    app.exception(Exception.class, (e, ctx) -> {
      logger.error("✗ Exceção não tratada - Path: {} Método: {}", ctx.path(), ctx.method(), e);

      ctx.status(500);
      ctx.json(new ErrorResponse("Internal Server Error", e.getMessage()));
    });

    // Handler para rotas não encontradas
    app.error(404, ctx -> {
      logger.debug("Rota não encontrada: {}", ctx.path());
      ctx.json(new ErrorResponse("Not Found", "Rota " + ctx.path() + " não existe"));
    });

    // Handler para acesso não autorizado
    app.error(401, ctx -> {
      logger.warn("✗ Não autorizado: {}", ctx.path());
      ctx.json(new ErrorResponse("Unauthorized", "Credenciais inválidas ou ausentes"));
    });

    // Handler para acesso proibido
    app.error(403, ctx -> {
      logger.warn("✗ Acesso proibido: {}", ctx.path());
      ctx.json(new ErrorResponse("Forbidden", "Você não tem permissão para acessar este recurso"));
    });
  }

  /**
   * Classe auxiliar para resposta de erro
   */
  private static class ErrorResponse {
    public String error;
    public String message;
    public long timestamp;

    ErrorResponse(String error, String message) {
      this.error = error;
      this.message = message;
      this.timestamp = System.currentTimeMillis();
    }
  }
}
