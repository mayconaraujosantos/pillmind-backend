package com.pillmind.presentation.controllers;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pillmind.data.protocols.cryptography.Decrypter;
import com.pillmind.domain.errors.UnauthorizedException;
import com.pillmind.domain.usecases.LoadUserById;
import com.pillmind.presentation.helpers.HttpHelper;
import com.pillmind.presentation.protocols.Controller;

import io.javalin.http.Context;

/**
 * Controller para obter perfil do usuário autenticado (GET /api/profile)
 */
public class ProfileController implements Controller {
  private final LoadUserById loadUserById;
  private final Decrypter decrypter;

  public ProfileController(LoadUserById loadUserById, Decrypter decrypter) {
    this.loadUserById = loadUserById;
    this.decrypter = decrypter;
  }

  @Override
  public void handle(Context ctx) {
    String token = extractAccessToken(ctx);
    if (token == null || token.isBlank()) {
      throw new UnauthorizedException("Token de acesso ausente");
    }

    final String userId;
    try {
      userId = decrypter.decrypt(token);
    } catch (Exception e) {
      throw new UnauthorizedException("Token inválido", e);
    }

    var user = loadUserById.execute(new LoadUserById.Params(userId));

    HttpHelper.ok(ctx, new ProfileResponse(
        user.id(),
        user.name(),
        user.email(),
        user.dateOfBirth(),
        user.gender() != null ? user.gender().name() : null,
        user.pictureUrl(),
        user.emailVerified(),
        user.updatedAt()));
  }

  private String extractAccessToken(Context ctx) {
    var authHeader = ctx.header("Authorization");
    if (authHeader != null) {
      // Accept "Bearer <token>" with case-insensitive scheme.
      if (authHeader.regionMatches(true, 0, "Bearer ", 0, 7)) {
        return authHeader.substring(7).trim();
      }
    }

    return ctx.header("x-access-token");
  }

  public record ProfileResponse(
      String id,
      String name,
      String email,
      @JsonFormat(pattern = "yyyy-MM-dd")
      LocalDate dateOfBirth,
      String gender,
      String pictureUrl,
      boolean emailVerified,
      @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
      LocalDateTime updatedAt
    ) {
  }
}
