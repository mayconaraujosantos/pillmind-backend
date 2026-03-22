package com.pillmind.presentation.controllers;

import com.pillmind.data.protocols.cryptography.Decrypter;
import com.pillmind.domain.errors.UnauthorizedException;
import com.pillmind.domain.usecases.LoadUserById;
import com.pillmind.domain.usecases.UpdateUserProfile;
import com.pillmind.presentation.helpers.HttpHelper;
import com.pillmind.presentation.protocols.Controller;

import io.javalin.http.Context;

/**
 * DELETE /api/profile/picture — remove a URL da foto no perfil (não apaga o objeto no MinIO).
 */
public class DeleteProfilePictureController implements Controller {

  private final UpdateUserProfile updateUserProfile;
  private final LoadUserById loadUserById;
  private final Decrypter decrypter;

  public DeleteProfilePictureController(
      UpdateUserProfile updateUserProfile,
      LoadUserById loadUserById,
      Decrypter decrypter) {
    this.updateUserProfile = updateUserProfile;
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

    var existingUser = loadUserById.execute(new LoadUserById.Params(userId));

    var params = new UpdateUserProfile.Params(
        userId,
        existingUser.name(),
        existingUser.email(),
        existingUser.dateOfBirth(),
        existingUser.gender(),
        null);

    var updated = updateUserProfile.execute(params);

    HttpHelper.ok(
        ctx,
        new ProfileController.ProfileResponse(
            updated.id(),
            updated.name(),
            updated.email(),
            updated.dateOfBirth(),
            updated.gender() != null ? updated.gender().name() : null,
            updated.pictureUrl(),
            updated.emailVerified(),
            updated.updatedAt()));
  }

  private String extractAccessToken(Context ctx) {
    var authHeader = ctx.header("Authorization");
    if (authHeader != null) {
      if (authHeader.regionMatches(true, 0, "Bearer ", 0, 7)) {
        return authHeader.substring(7).trim();
      }
    }
    return ctx.header("x-access-token");
  }

}
