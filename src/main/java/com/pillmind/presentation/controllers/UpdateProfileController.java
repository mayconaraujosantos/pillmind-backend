package com.pillmind.presentation.controllers;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pillmind.data.protocols.cryptography.Decrypter;
import com.pillmind.domain.errors.UnauthorizedException;
import com.pillmind.domain.errors.ValidationException;
import com.pillmind.domain.models.Gender;
import com.pillmind.domain.usecases.LoadUserById;
import com.pillmind.domain.usecases.UpdateUserProfile;
import com.pillmind.presentation.helpers.HttpHelper;
import com.pillmind.presentation.protocols.Controller;

import io.javalin.http.Context;

/**
 * Controller para atualizar perfil do usuário autenticado (PUT /api/profile)
 */
public class UpdateProfileController implements Controller {
    private final UpdateUserProfile updateUserProfile;
    private final LoadUserById loadUserById;
    private final Decrypter decrypter;
    private final ObjectMapper objectMapper;

    public UpdateProfileController(UpdateUserProfile updateUserProfile, LoadUserById loadUserById, Decrypter decrypter) {
        this.updateUserProfile = updateUserProfile;
        this.loadUserById = loadUserById;
        this.decrypter = decrypter;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
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

        // 3. Verificar se usuário existe
        var existingUser = loadUserById.execute(new LoadUserById.Params(userId));

        // 4. Parsear requisição
        try {
            var request = objectMapper.readValue(ctx.body(), UpdateProfileRequest.class);

            // 5. Validar dados da requisição
            validateRequest(request);

            // 6. Executar atualização
            var params = new UpdateUserProfile.Params(
                userId,
                request.name() != null ? request.name() : existingUser.name(),
                request.email() != null ? request.email() : existingUser.email(),
                request.dateOfBirth() != null ? request.dateOfBirth() : existingUser.dateOfBirth(),
                request.gender() != null ? Gender.fromString(request.gender()) : existingUser.gender(),
                request.pictureUrl() != null ? request.pictureUrl() : existingUser.pictureUrl()
            );

            var updatedUser = updateUserProfile.execute(params);

            // 7. Retornar resposta
            var response = new UpdateProfileResponse(
                updatedUser.id(),
                updatedUser.name(),
                updatedUser.email(),
                updatedUser.dateOfBirth(),
                updatedUser.gender() != null ? updatedUser.gender().name() : null,
                updatedUser.pictureUrl(),
                updatedUser.emailVerified(),
                updatedUser.updatedAt()
            );

            HttpHelper.ok(ctx, response);

        } catch (JsonProcessingException e) {
            throw new ValidationException("Formato JSON inválido na requisição", e);
        }
    }

    private void validateRequest(UpdateProfileRequest request) {
        // Validação básica - pelo menos um campo deve ser informado
        if (request.name() == null && request.email() == null && 
            request.dateOfBirth() == null && request.gender() == null && 
            request.pictureUrl() == null) {
            throw new ValidationException("Pelo menos um campo deve ser informado para atualização");
        }

        // Validar nome se informado
        if (request.name() != null && request.name().isBlank()) {
            throw new ValidationException("Nome não pode estar vazio");
        }

        // Validar email se informado
        if (request.email() != null && (request.email().isBlank() || !isValidEmail(request.email()))) {
            throw new ValidationException("Email inválido");
        }

        // Validar gênero se informado
        if (request.gender() != null && Gender.fromString(request.gender()) == null) {
            throw new ValidationException("Gênero inválido. Valores aceitos: MALE, FEMALE, OTHER, PREFER_NOT_TO_SAY");
        }
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");
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

    public record UpdateProfileRequest(
        String name,
        String email,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate dateOfBirth,
        String gender,
        String pictureUrl
    ) {}

    public record UpdateProfileResponse(
        String id,
        String name,
        String email,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate dateOfBirth,
        String gender,
        String pictureUrl,
        boolean emailVerified,
        java.time.LocalDateTime updatedAt
    ) {}
}