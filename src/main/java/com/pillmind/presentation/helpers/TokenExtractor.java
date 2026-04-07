package com.pillmind.presentation.helpers;

import io.javalin.http.Context;

public final class TokenExtractor {

    private TokenExtractor() {
    }

    public static String extractAccessToken(Context ctx) {
        var authHeader = ctx.header("Authorization");
        if (authHeader != null && authHeader.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return authHeader.substring(7).trim();
        }
        return ctx.header("x-access-token");
    }
}
