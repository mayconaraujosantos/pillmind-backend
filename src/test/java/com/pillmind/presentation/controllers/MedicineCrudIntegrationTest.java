package com.pillmind.presentation.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pillmind.main.routes.AuthRoutes;
import com.pillmind.main.routes.MedicineRoutes;
import com.pillmind.presentation.handlers.ErrorHandlers;
import com.pillmind.test.base.IntegrationTestBase;

import io.javalin.Javalin;
import io.javalin.testtools.HttpClient;
import io.javalin.testtools.JavalinTest;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * TDD: contrato HTTP do CRUD de medicamentos (autenticado por JWT).
 */
@DisplayName("Medicine CRUD Integration Tests")
class MedicineCrudIntegrationTest extends IntegrationTestBase {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final MediaType JSON = MediaType.parse("application/json");

    @Test
    @DisplayName("GET /api/medicines sem token retorna 401")
    void listWithoutTokenReturns401() {
        JavalinTest.test((app, client) -> {
            setupRoutes(app);
            try (Response response = client.request("/api/medicines", Request.Builder::get)) {
                assertEquals(401, response.code());
            }
        });
    }

    @Test
    @DisplayName("GET /api/medicines vazio após cadastro retorna []")
    void listEmptyReturnsOk() {
        JavalinTest.test((app, client) -> {
            setupRoutes(app);
            String token = signUpAndSignIn(client);
            try (Response response = client.request("/api/medicines", b -> {
                b.header("Authorization", "Bearer " + token);
                b.get();
            })) {
                String raw = response.body().string();
                assertEquals(200, response.code(), raw);
                JsonNode body = MAPPER.readTree(raw);
                assertTrue(body.isArray());
                assertEquals(0, body.size());
            }
        });
    }

    @Test
    @DisplayName("POST cria medicamento, GET lista, GET por id, PUT atualiza, DELETE remove")
    void fullCrudFlow() {
        JavalinTest.test((app, client) -> {
            setupRoutes(app);
            String token = signUpAndSignIn(client);

            String createBody = """
                    {
                      "name": "Paracetamol",
                      "dosage": "500mg",
                      "frequency": "twice-a-day",
                      "times": ["08:00", "20:00"],
                      "startDate": "2024-01-01",
                      "endDate": "2024-12-31",
                      "notes": "Com alimento"
                    }
                    """;

            String id;
            try (Response post = client.request("/api/medicines", b -> {
                b.header("Authorization", "Bearer " + token);
                b.post(RequestBody.create(createBody.getBytes(StandardCharsets.UTF_8), JSON));
            })) {
                String postBody = post.body().string();
                assertEquals(201, post.code(), postBody);
                JsonNode created = MAPPER.readTree(postBody);
                id = created.get("id").asText();
                assertEquals("Paracetamol", created.get("name").asText());
                assertEquals(2, created.get("times").size());
                assertEquals("capsule", created.get("medicineType").asText());
                assertEquals(1, created.get("quantity").asInt());
                assertTrue(created.get("reminderOnEmpty").asBoolean());
            }

            assertEquals(1, countMedicines(null));

            try (Response list = client.request("/api/medicines", b -> {
                b.header("Authorization", "Bearer " + token);
                b.get();
            })) {
                assertEquals(200, list.code());
                JsonNode arr = MAPPER.readTree(list.body().string());
                assertEquals(1, arr.size());
            }

            try (Response one = client.request("/api/medicines/" + id, b -> {
                b.header("Authorization", "Bearer " + token);
                b.get();
            })) {
                assertEquals(200, one.code());
                assertEquals(id, MAPPER.readTree(one.body().string()).get("id").asText());
            }

            String updateBody = """
                    {
                      "name": "Paracetamol 750",
                      "dosage": "750mg",
                      "frequency": "once-a-day",
                      "times": ["09:00"],
                      "startDate": "2024-02-01",
                      "notes": "Atualizado"
                    }
                    """;
            try (Response put = client.request("/api/medicines/" + id, b -> {
                b.header("Authorization", "Bearer " + token);
                b.put(RequestBody.create(updateBody.getBytes(StandardCharsets.UTF_8), JSON));
            })) {
                String putStr = put.body().string();
                assertEquals(200, put.code(), putStr);
                JsonNode updated = MAPPER.readTree(putStr);
                assertEquals("Paracetamol 750", updated.get("name").asText());
                assertTrue(updated.get("endDate").isNull());
            }

            try (Response del = client.request("/api/medicines/" + id, b -> {
                b.header("Authorization", "Bearer " + token);
                b.delete();
            })) {
                assertEquals(204, del.code());
            }

            try (Response gone = client.request("/api/medicines/" + id, b -> {
                b.header("Authorization", "Bearer " + token);
                b.get();
            })) {
                assertEquals(404, gone.code());
            }

            assertEquals(0, countMedicines(null));
        });
    }

    @Test
    @DisplayName("POST com nome vazio retorna 400")
    void createWithInvalidBodyReturns400() {
        JavalinTest.test((app, client) -> {
            setupRoutes(app);
            String token = signUpAndSignIn(client);
            String bad = """
                    { "name": "", "dosage": "1mg", "frequency": "daily", "times": [], "startDate": "2024-01-01" }
                    """;
            try (Response post = client.request("/api/medicines", b -> {
                b.header("Authorization", "Bearer " + token);
                b.post(RequestBody.create(bad.getBytes(StandardCharsets.UTF_8), JSON));
            })) {
                assertEquals(400, post.code());
            }
        });
    }

    private void setupRoutes(Javalin app) {
        ErrorHandlers.configure(app);
        try {
            container.resolve("route.auth", AuthRoutes.class).setup(app);
            container.resolve("route.medicines", MedicineRoutes.class).setup(app);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String signUpAndSignIn(HttpClient client) throws IOException {
        try (var signUp = client.post("/api/signup", """
                {
                  "name": "Med User",
                  "email": "med.user@example.com",
                  "password": "SecurePass123",
                  "dateOfBirth": "1990-01-01",
                  "gender": "MALE"
                }
                """)) {
            assertEquals(201, signUp.code(), signUp.body().string());
        }
        String json;
        try (var signIn = client.post("/api/signin", """
                { "email": "med.user@example.com", "password": "SecurePass123" }
                """)) {
            json = signIn.body().string();
            assertEquals(200, signIn.code(), json);
        }
        return MAPPER.readTree(json).get("accessToken").asText();
    }

    private int countMedicines(String whereClause) throws SQLException {
        String sql = "SELECT COUNT(*) FROM medicines";
        if (whereClause != null && !whereClause.isEmpty()) {
            sql += " WHERE " + whereClause;
        }
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
}
