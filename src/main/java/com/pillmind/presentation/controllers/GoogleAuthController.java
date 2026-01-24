package com.pillmind.presentation.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pillmind.domain.errors.ValidationException;
import com.pillmind.domain.usecases.AddAccount;
import com.pillmind.domain.usecases.Authentication;
import com.pillmind.infra.oauth.GoogleTokenValidator;
import com.pillmind.presentation.protocols.Controller;

import io.javalin.http.Context;

/**
 * Controller para autenticação via Google OAuth2
 * Recebe o ID Token do Google e cria/autentica o usuário
 */
public class GoogleAuthController implements Controller {
    private static final Logger logger = LoggerFactory.getLogger(GoogleAuthController.class);
    private final AddAccount addAccount;
    private final Authentication authentication;
    private final GoogleTokenValidator googleTokenValidator;
    private final ObjectMapper objectMapper;

    public GoogleAuthController(
            AddAccount addAccount,
            Authentication authentication,
            GoogleTokenValidator googleTokenValidator) {
        this.addAccount = addAccount;
        this.authentication = authentication;
        this.googleTokenValidator = googleTokenValidator;
        this.objectMapper = new ObjectMapper();
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

                // Tenta criar ou atualizar a conta Google
                var params = new AddAccount.Params(
                    googleUserInfo.name(),
                    googleUserInfo.email(),
                    null, // Google accounts não têm senha
                    true, // googleAccount = true
                    googleUserInfo.googleId(),
                    googleUserInfo.pictureUrl());

                var account = addAccount.execute(params);

                // Autentica sempre após criar/atualizar
                var authResult = authentication.execute(
                    new Authentication.Params(account.email(), null));

                ctx.status(200).json(new GoogleAuthResponse(
                    authResult.accessToken(),
                    account.id(),
                    account.name(),
                    account.email()));
        } catch (JsonProcessingException e) {
            throw new ValidationException("Formato JSON inválido na requisição", e);
        }
    }

    public record GoogleAuthRequest(String idToken) {
    }

    public record GoogleAuthResponse(
            String accessToken,
            String accountId,
            String name,
            String email) {
    }
}
