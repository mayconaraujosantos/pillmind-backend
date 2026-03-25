package com.pillmind.presentation.helpers;

import com.pillmind.domain.errors.UnauthorizedException;

import io.javalin.http.Context;

/**
 * Extrai token JWT dos headers (Authorization Bearer ou x-access-token).
 */
public final class AccessTokenExtractor {

    private AccessTokenExtractor() {
    }

    public static String requireAccessToken(Context ctx) {
        String token = extractRaw(ctx);
        if (token == null || token.isBlank()) {
            throw new UnauthorizedException("Token de acesso ausente");
        }
        return token;
    }

    private static String extractRaw(Context ctx) {
        var authHeader = ctx.header("Authorization");
        if (authHeader != null && authHeader.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return authHeader.substring(7).trim();
        }
        return ctx.header("x-access-token");
    }
}
