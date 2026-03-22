package com.pillmind.presentation.controllers;

import java.io.InputStream;
import java.util.Set;

import com.pillmind.data.protocols.storage.ObjectStorageService;
import com.pillmind.data.protocols.cryptography.Decrypter;
import com.pillmind.domain.errors.UnauthorizedException;
import com.pillmind.domain.errors.ValidationException;
import com.pillmind.domain.usecases.LoadUserById;
import com.pillmind.domain.usecases.UpdateUserProfile;
import com.pillmind.presentation.helpers.HttpHelper;
import com.pillmind.presentation.protocols.Controller;

import io.javalin.http.Context;
import io.javalin.http.UploadedFile;

/**
 * POST /api/profile/picture — multipart field {@code file} (image/jpeg, image/png, image/webp).
 */
public class UploadProfilePictureController implements Controller {

  private static final long MAX_BYTES = 5 * 1024 * 1024;
  private static final Set<String> ALLOWED_TYPES = Set.of(
      "image/jpeg",
      "image/jpg",
      "image/png",
      "image/webp");

  private final ObjectStorageService objectStorage;
  private final UpdateUserProfile updateUserProfile;
  private final LoadUserById loadUserById;
  private final Decrypter decrypter;

  public UploadProfilePictureController(
      ObjectStorageService objectStorage,
      UpdateUserProfile updateUserProfile,
      LoadUserById loadUserById,
      Decrypter decrypter) {
    this.objectStorage = objectStorage;
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

    UploadedFile file = ctx.uploadedFile("file");
    if (file == null) {
      throw new ValidationException("Campo multipart 'file' é obrigatório");
    }

    String contentType = file.contentType();
    if (contentType != null) {
      contentType = contentType.split(";")[0].trim().toLowerCase();
    }
    if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
      throw new ValidationException("Tipo de arquivo não suportado. Use JPEG, PNG ou WebP.");
    }

    long size = file.size();
    if (size <= 0 || size > MAX_BYTES) {
      throw new ValidationException("Arquivo deve ter entre 1 byte e 5 MB.");
    }

    var existingUser = loadUserById.execute(new LoadUserById.Params(userId));

    ObjectStorageService.StoredObject stored;
    try (InputStream in = file.content()) {
      stored = objectStorage.putProfileImage(in, size, contentType, userId);
    } catch (Exception e) {
      if (e instanceof RuntimeException re) {
        throw re;
      }
      throw new ValidationException("Erro ao ler o arquivo enviado", e);
    }

    var params = new UpdateUserProfile.Params(
        userId,
        existingUser.name(),
        existingUser.email(),
        existingUser.dateOfBirth(),
        existingUser.gender(),
        stored.publicUrl());

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
