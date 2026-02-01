package com.pillmind.presentation.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pillmind.domain.errors.ConflictException;
import com.pillmind.domain.errors.NotFoundException;
import com.pillmind.domain.errors.UnauthorizedException;
import com.pillmind.domain.errors.ValidationException;
import com.pillmind.presentation.helpers.HttpHelper;

import io.javalin.Javalin;

/**
 * Configura handlers globais de erro para a API.
 */
public final class ErrorHandlers {
    private static final Logger logger = LoggerFactory.getLogger(ErrorHandlers.class);

    private ErrorHandlers() {
    }

    public static void configure(Javalin app) {
        app.exception(ValidationException.class, (e, ctx) -> {
            logger.debug("Validation error: {}", e.getMessage());
            HttpHelper.badRequest(ctx, e.getMessage());
        });

        app.exception(UnauthorizedException.class, (e, ctx) -> {
            logger.debug("Unauthorized: {}", e.getMessage());
            HttpHelper.unauthorized(ctx, e.getMessage());
        });

        app.exception(ConflictException.class, (e, ctx) -> {
            logger.debug("Conflict: {}", e.getMessage());
            HttpHelper.conflict(ctx, e.getMessage());
        });

        app.exception(NotFoundException.class, (e, ctx) -> {
            logger.debug("Not found: {}", e.getMessage());
            HttpHelper.notFound(ctx, e.getMessage());
        });

        app.exception(Exception.class, (e, ctx) -> {
            logger.error("Unhandled exception - Path: {} Method: {}", ctx.path(), ctx.method(), e);
            HttpHelper.serverError(ctx, "Erro interno do servidor");
        });

        // Handler para rotas não encontradas
        app.error(404, ctx -> {
            logger.debug("Rota não encontrada: {}", ctx.path());
            HttpHelper.notFound(ctx, "Rota " + ctx.path() + " não existe");
        });

        // Handler para acesso não autorizado
        app.error(401, ctx -> {
            logger.warn("Não autorizado: {}", ctx.path());
            HttpHelper.unauthorized(ctx, "Credenciais inválidas ou ausentes");
        });

        // Handler para acesso proibido
        app.error(403, ctx -> {
            logger.warn("Acesso proibido: {}", ctx.path());
            HttpHelper.forbidden(ctx, "Você não tem permissão para acessar este recurso");
        });
    }
}
