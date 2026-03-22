package com.pillmind.presentation.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Base64;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pillmind.main.routes.AuthRoutes;
import com.pillmind.presentation.handlers.ErrorHandlers;
import com.pillmind.test.base.IntegrationTestBase;

import io.javalin.Javalin;
import io.javalin.testtools.HttpClient;
import io.javalin.testtools.JavalinTest;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Garante que o upload de foto de perfil está ligado às rotas.
 * Com {@code MINIO_ENABLED=false} no JVM de teste, espera-se 503; com MinIO ok, 200.
 */
@DisplayName("Profile picture / MinIO route")
class ProfilePictureRouteIntegrationTest extends IntegrationTestBase {

  private static final byte[] PNG_1x1 = Base64.getDecoder().decode(
      "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8z8BQDwAEhQGAhKmMIQAAAABJRU5ErkJggg==");

  @Test
  @DisplayName("POST /api/profile/picture não retorna 404 (rota registrada)")
  void profilePictureRouteIsRegistered() throws Exception {
    JavalinTest.test((app, client) -> {
      setupRoutes(app);
      String token = signUpAndSignIn(client);

      RequestBody fileBody = RequestBody.create(PNG_1x1, MediaType.parse("image/png"));
      MultipartBody body = new MultipartBody.Builder()
          .setType(MultipartBody.FORM)
          .addFormDataPart("file", "avatar.png", fileBody)
          .build();

      try (Response resp = client.request("/api/profile/picture", b -> {
        b.header("x-access-token", token);
        b.post(body);
      })) {
        assertNotEquals(
            404,
            resp.code(),
            "Se aparecer 404, o servidor em execução está com JAR antigo — faça build e reinicie.");
        assertTrue(
            resp.code() == 200 || resp.code() == 503,
            "Esperado 200 (MinIO ok) ou 503 (armazenamento desligado/indisponível), mas foi "
                + resp.code());
      }
    });
  }

  private void setupRoutes(Javalin app) {
    ErrorHandlers.configure(app);
    var authRoutes = container.resolve("route.auth", AuthRoutes.class);
    try {
      authRoutes.setup(app);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private String signUpAndSignIn(HttpClient client) throws IOException {
    try (var signUp = client.post("/api/signup", """
        {"name":"Pic User","email":"picuser@example.com","password":"test12"}
        """)) {
      String signUpBody = signUp.body().string();
      assertEquals(201, signUp.code(), signUpBody);
    }
    String json;
    try (var signIn = client.post("/api/signin", """
        {"email":"picuser@example.com","password":"test12"}
        """)) {
      assertEquals(200, signIn.code());
      json = signIn.body().string();
    }
    return new ObjectMapper().readTree(json).get("accessToken").asText();
  }
}
