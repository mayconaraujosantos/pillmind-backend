package com.pillmind.presentation.helpers;

import io.javalin.http.Context;

import java.util.Map;

/**
 * Helper para criar respostas HTTP padronizadas
 */
public class HttpHelper {
    public static void ok(Context ctx, Object data) {
        ctx.status(200).json(data);
    }

    public static void created(Context ctx, Object data) {
        ctx.status(201).json(data);
    }

    public static void noContent(Context ctx) {
        ctx.status(204);
    }

    public static void badRequest(Context ctx, String message) {
        ctx.status(400).json(Map.of("error", message));
    }

    public static void unauthorized(Context ctx, String message) {
        ctx.status(401).json(Map.of("error", message));
    }

    public static void forbidden(Context ctx, String message) {
        ctx.status(403).json(Map.of("error", message));
    }

    public static void notFound(Context ctx, String message) {
        ctx.status(404).json(Map.of("error", message));
    }

    public static void conflict(Context ctx, String message) {
        ctx.status(409).json(Map.of("error", message));
    }

    public static void serverError(Context ctx, String message) {
        ctx.status(500).json(Map.of("error", message));
    }
}
