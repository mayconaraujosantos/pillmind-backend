package com.pillmind.infra.oauth;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

/**
 * Validador de tokens do Google OAuth2
 * Valida idToken e extrai informações do usuário
 */
public class GoogleTokenValidator {
    private static final Logger logger = LoggerFactory.getLogger(GoogleTokenValidator.class);
    private final GoogleIdTokenVerifier verifier;

    public GoogleTokenValidator(String clientId) {
        this.verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                GsonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(clientId))
                .build();
    }

    /**
     * Valida o ID Token do Google e retorna informações do usuário
     * 
     * @param idTokenString Token JWT recebido do cliente
     * @return Informações validadas do usuário
     * @throws RuntimeException se o token for inválido
     */
    public GoogleUserInfo validate(String idTokenString) {
        try {
            GoogleIdToken idToken = verifier.verify(idTokenString);

            if (idToken == null) {
                throw new RuntimeException("Token do Google inválido ou expirado");
            }

            GoogleIdToken.Payload payload = idToken.getPayload();

            String userId = payload.getSubject();
            String email = payload.getEmail();
            String name = (String) payload.get("name");
            String pictureUrl = (String) payload.get("picture");
            Boolean emailVerified = payload.getEmailVerified();

            if (!emailVerified) {
                logger.warn("Tentativa de login com email não verificado: {}", email);
                throw new RuntimeException("Email do Google não verificado");
            }

            logger.info("Token Google validado com sucesso para: {}", email);
            return new GoogleUserInfo(userId, email, name, pictureUrl);

        } catch (GeneralSecurityException | IOException e) {
            logger.error("Erro ao validar token do Google: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao validar token do Google: " + e.getMessage());
        }
    }

    /**
     * Informações do usuário extraídas do token do Google
     */
    public record GoogleUserInfo(
            String googleId,
            String email,
            String name,
            String pictureUrl) {
    }
}
