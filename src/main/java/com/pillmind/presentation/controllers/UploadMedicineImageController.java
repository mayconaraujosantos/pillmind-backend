package com.pillmind.presentation.controllers;

import java.io.InputStream;
import java.util.Map;
import java.util.Set;

import com.pillmind.data.protocols.cryptography.Decrypter;
import com.pillmind.data.protocols.storage.ObjectStorageService;
import com.pillmind.domain.errors.UnauthorizedException;
import com.pillmind.domain.errors.ValidationException;
import com.pillmind.presentation.helpers.AccessTokenExtractor;
import com.pillmind.presentation.helpers.HttpHelper;
import com.pillmind.presentation.protocols.Controller;

import io.javalin.http.Context;
import io.javalin.http.UploadedFile;

/**
 * POST /api/medicines/picture — multipart {@code file}; devolve {@code imageUrl} para guardar no medicamento.
 */
public class UploadMedicineImageController implements Controller {

    private static final long MAX_BYTES = 5 * 1024L * 1024L;
    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg",
            "image/jpg",
            "image/png",
            "image/webp");

    private final ObjectStorageService objectStorage;
    private final Decrypter decrypter;

    public UploadMedicineImageController(
            ObjectStorageService objectStorage,
            Decrypter decrypter) {
        this.objectStorage = objectStorage;
        this.decrypter = decrypter;
    }

    @Override
    public void handle(Context ctx) {
        String token = AccessTokenExtractor.requireAccessToken(ctx);
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

        ObjectStorageService.StoredObject stored;
        try (InputStream in = file.content()) {
            stored = objectStorage.putMedicineImage(in, size, contentType, userId);
        } catch (Exception e) {
            if (e instanceof RuntimeException re) {
                throw re;
            }
            throw new ValidationException("Erro ao ler o arquivo enviado", e);
        }

        HttpHelper.ok(ctx, Map.of("imageUrl", stored.publicUrl()));
    }
}
