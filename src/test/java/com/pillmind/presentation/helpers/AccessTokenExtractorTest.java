package com.pillmind.presentation.helpers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.pillmind.presentation.handlers.ErrorHandlers;

import io.javalin.testtools.JavalinTest;
import okhttp3.Request;

@DisplayName("AccessTokenExtractor")
class AccessTokenExtractorTest {

    @Test
    @DisplayName("Should extract token from Authorization Bearer header")
    void shouldExtractTokenFromAuthorizationHeader() {
        JavalinTest.test((app, client) -> {
            app.get("/test", ctx -> {
                var token = AccessTokenExtractor.requireAccessToken(ctx);
                ctx.result(token);
            });

            try (var response = client.request("/test", b -> {
                b.header("Authorization", "Bearer my-token-123");
                b.get();
            })) {
                assertEquals(200, response.code());
                assertEquals("my-token-123", response.body().string());
            }
        });
    }

    @Test
    @DisplayName("Should extract token from x-access-token header")
    void shouldExtractTokenFromXAccessTokenHeader() {
        JavalinTest.test((app, client) -> {
            app.get("/test", ctx -> {
                var token = AccessTokenExtractor.requireAccessToken(ctx);
                ctx.result(token);
            });

            try (var response = client.request("/test", b -> {
                b.header("x-access-token", "my-x-token-456");
                b.get();
            })) {
                assertEquals(200, response.code());
                assertEquals("my-x-token-456", response.body().string());
            }
        });
    }

    @Test
    @DisplayName("Should prefer Authorization header over x-access-token")
    void shouldPreferAuthorizationOverXAccessToken() {
        JavalinTest.test((app, client) -> {
            app.get("/test", ctx -> {
                var token = AccessTokenExtractor.requireAccessToken(ctx);
                ctx.result(token);
            });

            try (var response = client.request("/test", b -> {
                b.header("Authorization", "Bearer bearer-token");
                b.header("x-access-token", "x-token");
                b.get();
            })) {
                assertEquals(200, response.code());
                assertEquals("bearer-token", response.body().string());
            }
        });
    }

    @Test
    @DisplayName("Should throw UnauthorizedException when no token is present")
    void shouldThrowWhenNoTokenPresent() {
        JavalinTest.test((app, client) -> {
            ErrorHandlers.configure(app);
            app.get("/test", ctx -> AccessTokenExtractor.requireAccessToken(ctx));

            var response = client.get("/test");

            assertEquals(401, response.code());
        });
    }

    @Test
    @DisplayName("Should be case-insensitive for Bearer prefix")
    void shouldBeCaseInsensitiveForBearerPrefix() {
        JavalinTest.test((app, client) -> {
            app.get("/test", ctx -> {
                var token = AccessTokenExtractor.requireAccessToken(ctx);
                ctx.result(token);
            });

            try (var response = client.request("/test", b -> {
                b.header("Authorization", "bearer lowercase-token");
                b.get();
            })) {
                assertEquals(200, response.code());
                assertEquals("lowercase-token", response.body().string());
            }
        });
    }
}
