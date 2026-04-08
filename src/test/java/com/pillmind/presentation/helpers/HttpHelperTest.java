package com.pillmind.presentation.helpers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.api.Test;

import io.javalin.testtools.JavalinTest;

/**
 * Testes para HttpHelper
 */
class HttpHelperTest {
    @Test
    void testOkResponse() {
        JavalinTest.test((app, client) -> {
            app.get("/test", ctx -> HttpHelper.ok(ctx, Map.of("message", "success")));

            var response = client.get("/test");
            assertEquals(200, response.code());
        });
    }
}
