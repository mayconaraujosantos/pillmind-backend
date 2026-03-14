package com.pillmind.presentation.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pillmind.data.protocols.cryptography.Decrypter;
import com.pillmind.domain.errors.UnauthorizedException;
import com.pillmind.domain.errors.ValidationException;
import com.pillmind.domain.models.ImageKind;
import com.pillmind.domain.usecases.RequestImageUpload;
import com.pillmind.presentation.helpers.HttpHelper;
import com.pillmind.presentation.protocols.Controller;

import io.javalin.http.Context;

public class RequestImageUploadController implements Controller {
    private final RequestImageUpload requestImageUpload;
    private final Decrypter decrypter;
    private final ObjectMapper objectMapper;

    public RequestImageUploadController(RequestImageUpload requestImageUpload, Decrypter decrypter) {
        this.requestImageUpload = requestImageUpload;
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
            var request = objectMapper.readValue(ctx.body(), RequestImageUploadRequest.class);
            ImageKind imageKind = ImageKind.fromString(request.kind());

            var result = requestImageUpload.execute(new RequestImageUpload.Params(
                    userId,
                    imageKind,
                    request.fileName(),
                    request.contentType(),
                    request.size()));

            HttpHelper.ok(ctx, new RequestImageUploadResponse(result.imageId(), result.uploadUrl()));
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

    public record RequestImageUploadRequest(
            String kind,
            String fileName,
            String contentType,
            long size) {
    }

    public record RequestImageUploadResponse(
            String imageId,
            String uploadUrl) {
    }
}
