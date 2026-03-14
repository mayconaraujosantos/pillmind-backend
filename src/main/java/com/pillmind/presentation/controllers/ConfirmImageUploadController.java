package com.pillmind.presentation.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pillmind.data.protocols.cryptography.Decrypter;
import com.pillmind.domain.errors.UnauthorizedException;
import com.pillmind.domain.errors.ValidationException;
import com.pillmind.domain.models.ImageKind;
import com.pillmind.domain.usecases.ConfirmImageUpload;
import com.pillmind.domain.usecases.LoadUserById;
import com.pillmind.domain.usecases.UpdateUserProfile;
import com.pillmind.presentation.helpers.HttpHelper;
import com.pillmind.presentation.protocols.Controller;

import io.javalin.http.Context;

public class ConfirmImageUploadController implements Controller {
    private final ConfirmImageUpload confirmImageUpload;
    private final UpdateUserProfile updateUserProfile;
    private final LoadUserById loadUserById;
    private final Decrypter decrypter;
    private final ObjectMapper objectMapper;

    public ConfirmImageUploadController(
            ConfirmImageUpload confirmImageUpload,
            UpdateUserProfile updateUserProfile,
            LoadUserById loadUserById,
            Decrypter decrypter) {
        this.confirmImageUpload = confirmImageUpload;
        this.updateUserProfile = updateUserProfile;
        this.loadUserById = loadUserById;
        this.decrypter = decrypter;
        this.objectMapper = new ObjectMapper();
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

        try {
            var request = objectMapper.readValue(ctx.body(), ConfirmImageUploadRequest.class);
            ImageKind imageKind = ImageKind.fromString(request.kind());

            var result = confirmImageUpload
                    .execute(new ConfirmImageUpload.Params(userId, imageKind, request.imageId()));

            if (imageKind == ImageKind.PROFILE) {
                var existingUser = loadUserById.execute(new LoadUserById.Params(userId));
                updateUserProfile.execute(new UpdateUserProfile.Params(
                        userId,
                        existingUser.name(),
                        existingUser.email(),
                        existingUser.dateOfBirth(),
                        existingUser.gender(),
                        result.deliveryUrl()));
            }

            HttpHelper.ok(ctx,
                    new ConfirmImageUploadResponse(result.imageId(), result.deliveryUrl(), imageKind.name()));
        } catch (JsonProcessingException e) {
            throw new ValidationException("Formato JSON inválido na requisição", e);
        }
    }

    private String extractAccessToken(Context ctx) {
        var authHeader = ctx.header("Authorization");
        if (authHeader != null && authHeader.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return authHeader.substring(7).trim();
        }
        return ctx.header("x-access-token");
    }

    public record ConfirmImageUploadRequest(
            String kind,
            String imageId) {
    }

    public record ConfirmImageUploadResponse(
            String imageId,
            String imageUrl,
            String kind) {
    }
}
