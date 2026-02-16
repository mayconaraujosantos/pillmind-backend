package com.pillmind.presentation.controllers;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pillmind.data.protocols.cryptography.Encrypter;
import com.pillmind.domain.errors.ValidationException;
import com.pillmind.domain.models.AuthProvider;
import com.pillmind.domain.usecases.LinkOAuthAccount;
import com.pillmind.infra.oauth.GoogleTokenValidator;
import com.pillmind.presentation.protocols.Controller;

import io.javalin.http.Context;

/**
 * Controller para autenticação via Google OAuth2 - Nova estrutura
 * Recebe o ID Token do Google e cria/autentica o usuário
 */
public class GoogleAuthController implements Controller {
    private static final Logger logger = LoggerFactory.getLogger(GoogleAuthController.class);
    private final LinkOAuthAccount linkOAuthAccount;
    private final Encrypter encrypter;
    private final GoogleTokenValidator googleTokenValidator;
    private final ObjectMapper objectMapper;

    public GoogleAuthController(
            LinkOAuthAccount linkOAuthAccount,
            Encrypter encrypter,
            GoogleTokenValidator googleTokenValidator) {
        this.linkOAuthAccount = linkOAuthAccount;
        this.encrypter = encrypter;
        this.googleTokenValidator = googleTokenValidator;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public void handle(Context ctx) {
        try {
            var request = objectMapper.readValue(ctx.body(), GoogleAuthRequest.class);

            if (request.idToken() == null || request.idToken().isBlank()) {
                throw new ValidationException("O campo 'idToken' é obrigatório");
            }

            // Valida token com Google e extrai dados do usuário
            var googleUserInfo = googleTokenValidator.validate(request.idToken());
            logger.info("Login Google para: {}", googleUserInfo.email());

            // Vincular/criar conta OAuth2
            var params = new LinkOAuthAccount.Params(
                AuthProvider.GOOGLE,
                googleUserInfo.googleId(),
                googleUserInfo.email(),
                googleUserInfo.name(),
                googleUserInfo.pictureUrl());

            var result = linkOAuthAccount.execute(params);
            
            // Gerar token de acesso
            var accessToken = encrypter.encrypt(result.user().id());

            var response = new GoogleAuthResponse(
                    accessToken,
                    result.user().id(),
                    result.user().name(),
                    result.user().email(),
                    result.user().dateOfBirth(),
                    result.user().gender() != null ? result.user().gender().name() : null,
                    result.user().pictureUrl(),
                    result.user().emailVerified(),
                    result.isNewUser());

            ctx.status(200).json(response);
        } catch (JsonProcessingException e) {
            throw new ValidationException("Formato JSON inválido na requisição", e);
        }
    }

    public record GoogleAuthRequest(String idToken) {
    }

    public record GoogleAuthResponse(
            String accessToken,
            String userId,
            String name,
            String email,
            @JsonFormat(pattern = "yyyy-MM-dd")
            LocalDate dateOfBirth,
            String gender,
            String pictureUrl,
            boolean emailVerified,
            boolean isNewUser) {
    }
}
