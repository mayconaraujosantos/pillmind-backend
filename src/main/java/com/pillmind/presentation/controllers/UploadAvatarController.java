package com.pillmind.presentation.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pillmind.data.protocols.cryptography.Decrypter;
import com.pillmind.domain.errors.UnauthorizedException;
import com.pillmind.domain.errors.ValidationException;
import com.pillmind.domain.usecases.LoadUserById;
import com.pillmind.domain.usecases.UpdateUserProfile;
import com.pillmind.main.config.Env;
import com.pillmind.presentation.helpers.HttpHelper;
import com.pillmind.presentation.protocols.Controller;

import io.javalin.http.Context;
import io.javalin.http.UploadedFile;

/**
 * Controller para upload de avatar do usuário (POST /api/profile/avatar)
 */
public class UploadAvatarController implements Controller {
    private static final Logger logger = LoggerFactory.getLogger(UploadAvatarController.class);

    private static final String UPLOAD_DIR = "uploads/avatars";
    private static final long MAX_FILE_SIZE = 5L * 1024 * 1024; // 5MB
    private static final String[] ALLOWED_EXTENSIONS = { ".jpg", ".jpeg", ".png", ".gif", ".webp" };

    private final UpdateUserProfile updateUserProfile;
    private final LoadUserById loadUserById;
    private final Decrypter decrypter;

    public UploadAvatarController(
            UpdateUserProfile updateUserProfile,
            LoadUserById loadUserById,
            Decrypter decrypter) {
        this.updateUserProfile = updateUserProfile;
        this.loadUserById = loadUserById;
        this.decrypter = decrypter;
    }

    @Override
    public void handle(Context ctx) {
        // 1. Extrair e validar token
        String token = extractAccessToken(ctx);
        if (token == null || token.isBlank()) {
            throw new UnauthorizedException("Token de acesso ausente");
        }

        // 2. Decriptar token para obter ID do usuário
        final String userId;
        try {
            userId = decrypter.decrypt(token);
        } catch (Exception e) {
            throw new UnauthorizedException("Token inválido", e);
        }

        // 3. Buscar usuário existente
        var existingUser = loadUserById.execute(new LoadUserById.Params(userId));

        // 4. Processar upload do arquivo
        UploadedFile uploadedFile = ctx.uploadedFile("avatar");
        if (uploadedFile == null) {
            throw new ValidationException("Nenhum arquivo foi enviado. Use o campo 'avatar' no form-data.");
        }

        // 5. Validar arquivo
        validateFile(uploadedFile);

        // 6. Salvar arquivo
        String savedFileName = saveFile(uploadedFile);

        // 7. Gerar URL pública
        String baseUrl = resolveBaseUrl(ctx);
        String pictureUrl = baseUrl + "/uploads/avatars/" + savedFileName;

        // 8. Atualizar perfil do usuário com nova URL da foto
        var params = new UpdateUserProfile.Params(
                userId,
                existingUser.name(),
                existingUser.email(),
                existingUser.dateOfBirth(),
                existingUser.gender(),
                pictureUrl);

        var updatedUser = updateUserProfile.execute(params);

        // 9. Retornar resposta
        HttpHelper.ok(ctx, new AvatarUploadResponse(updatedUser.pictureUrl()));
        logger.info("✓ Avatar uploaded successfully for user: {}", userId);
    }

    /**
     * Valida o arquivo enviado
     */
    private void validateFile(UploadedFile file) {
        // Validar tamanho
        if (file.size() > MAX_FILE_SIZE) {
            throw new ValidationException(
                    String.format("Arquivo muito grande. Tamanho máximo: %d MB", MAX_FILE_SIZE / (1024 * 1024)));
        }

        // Validar extensão
        String filename = file.filename();
        boolean isValidExtension = false;
        for (String ext : ALLOWED_EXTENSIONS) {
            if (filename.toLowerCase().endsWith(ext)) {
                isValidExtension = true;
                break;
            }
        }

        if (!isValidExtension) {
            throw new ValidationException(
                    "Formato de arquivo não suportado. Use: " + String.join(", ", ALLOWED_EXTENSIONS));
        }

        // Validar content type
        String contentType = file.contentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new ValidationException("O arquivo deve ser uma imagem");
        }
    }

    /**
     * Salva o arquivo no disco e retorna o nome do arquivo salvo
     */
    private String saveFile(UploadedFile file) {
        try {
            // Criar diretório se não existir
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                logger.info("Created upload directory: {}", UPLOAD_DIR);
            }

            // Gerar nome único para o arquivo
            String originalFilename = file.filename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueFilename = UUID.randomUUID().toString() + extension;

            // Salvar arquivo
            Path filePath = uploadPath.resolve(uniqueFilename);
            try (InputStream inputStream = file.content()) {
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            }

            logger.info("✓ File saved: {}", uniqueFilename);
            return uniqueFilename;

        } catch (IOException e) {
            logger.error("Error saving file: {}", e.getMessage(), e);
            throw new IllegalStateException(
                    "Erro ao salvar arquivo '" + file.filename() + "' em '" + UPLOAD_DIR + "'",
                    e);
        }
    }

    /**
     * Extrai o token de acesso do header Authorization
     */
    private String extractAccessToken(Context ctx) {
        var authHeader = ctx.header("Authorization");
        if (authHeader != null && authHeader.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return authHeader.substring(7).trim();
        }
        return ctx.header("x-access-token");
    }

    /**
     * Resolve base URL for public assets.
     * Priority:
     * 1) Env.BASE_URL if configured
     * 2) X-Forwarded-Proto + X-Forwarded-Host (reverse proxy)
     * 3) Request scheme + host
     */
    private String resolveBaseUrl(Context ctx) {
        if (Env.BASE_URL != null && !Env.BASE_URL.isBlank()) {
            return Env.BASE_URL;
        }

        String forwardedProto = ctx.header("X-Forwarded-Proto");
        String forwardedHost = ctx.header("X-Forwarded-Host");

        if (forwardedProto != null && !forwardedProto.isBlank()
                && forwardedHost != null && !forwardedHost.isBlank()) {
            return forwardedProto + "://" + forwardedHost;
        }

        return ctx.scheme() + "://" + ctx.host();
    }

    /**
     * Response do upload de avatar
     */
    public record AvatarUploadResponse(String pictureUrl) {
    }
}
