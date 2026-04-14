package com.pillmind.presentation.helpers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.javalin.testtools.JavalinTest;

@DisplayName("HttpHelper")
class HttpHelperAllMethodsTest {

    @Test
    @DisplayName("ok() should return status 200 with body")
    void testOkResponse() {
        JavalinTest.test((app, client) -> {
            app.get("/test-ok", ctx -> HttpHelper.ok(ctx, Map.of("key", "value")));
            var response = client.get("/test-ok");
            assertEquals(200, response.code());
            assertTrue(response.body().string().contains("value"));
        });
    }

    @Test
    @DisplayName("created() should return status 201 with body")
    void testCreatedResponse() {
        JavalinTest.test((app, client) -> {
            app.post("/test-created", ctx -> HttpHelper.created(ctx, Map.of("id", "new-id")));
            var response = client.post("/test-created", "{}");
            assertEquals(201, response.code());
            assertTrue(response.body().string().contains("new-id"));
        });
    }

    @Test
    @DisplayName("noContent() should return status 204 with no body")
    void testNoContentResponse() {
        JavalinTest.test((app, client) -> {
            app.delete("/test-no-content", ctx -> HttpHelper.noContent(ctx));
            var response = client.delete("/test-no-content");
            assertEquals(204, response.code());
        });
    }

    @Test
    @DisplayName("badRequest() should return status 400 with error message")
    void testBadRequestResponse() {
        JavalinTest.test((app, client) -> {
            app.get("/test-bad-request", ctx -> HttpHelper.badRequest(ctx, "Invalid input"));
            var response = client.get("/test-bad-request");
            assertEquals(400, response.code());
            assertTrue(response.body().string().contains("Invalid input"));
        });
    }

    @Test
    @DisplayName("unauthorized() should return status 401 with error message")
    void testUnauthorizedResponse() {
        JavalinTest.test((app, client) -> {
            app.get("/test-unauthorized", ctx -> HttpHelper.unauthorized(ctx, "Not authorized"));
            var response = client.get("/test-unauthorized");
            assertEquals(401, response.code());
            assertTrue(response.body().string().contains("Not authorized"));
        });
    }

    @Test
    @DisplayName("forbidden() should return status 403 with error message")
    void testForbiddenResponse() {
        JavalinTest.test((app, client) -> {
            app.get("/test-forbidden", ctx -> HttpHelper.forbidden(ctx, "Forbidden"));
            var response = client.get("/test-forbidden");
            assertEquals(403, response.code());
            assertTrue(response.body().string().contains("Forbidden"));
        });
    }

    @Test
    @DisplayName("notFound() should return status 404 with error message")
    void testNotFoundResponse() {
        JavalinTest.test((app, client) -> {
            app.get("/test-not-found", ctx -> HttpHelper.notFound(ctx, "Resource not found"));
            var response = client.get("/test-not-found");
            assertEquals(404, response.code());
            assertTrue(response.body().string().contains("Resource not found"));
        });
    }

    @Test
    @DisplayName("conflict() should return status 409 with error message")
    void testConflictResponse() {
        JavalinTest.test((app, client) -> {
            app.post("/test-conflict", ctx -> HttpHelper.conflict(ctx, "Already exists"));
            var response = client.post("/test-conflict", "{}");
            assertEquals(409, response.code());
            assertTrue(response.body().string().contains("Already exists"));
        });
    }

    @Test
    @DisplayName("serverError() should return status 500 with error message")
    void testServerErrorResponse() {
        JavalinTest.test((app, client) -> {
            app.get("/test-server-error", ctx -> HttpHelper.serverError(ctx, "Internal error"));
            var response = client.get("/test-server-error");
            assertEquals(500, response.code());
            assertTrue(response.body().string().contains("Internal error"));
        });
    }

    @Test
    @DisplayName("serviceUnavailable() should return status 503 with error message")
    void testServiceUnavailableResponse() {
        JavalinTest.test((app, client) -> {
            app.get("/test-unavailable", ctx -> HttpHelper.serviceUnavailable(ctx, "Service down"));
            var response = client.get("/test-unavailable");
            assertEquals(503, response.code());
            assertTrue(response.body().string().contains("Service down"));
        });
    }
}
