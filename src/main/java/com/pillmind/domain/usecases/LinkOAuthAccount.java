package com.pillmind.domain.usecases;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.pillmind.domain.models.AuthProvider;
import com.pillmind.domain.models.Gender;
import com.pillmind.domain.models.User;

/**
 * Use Case para vincular/criar conta OAuth2
 * Se usuário não existir, cria User + OAuthAccount
 * Se existir, apenas vincula novo OAuthAccount ou atualiza existente
 */
public interface LinkOAuthAccount extends UseCase<LinkOAuthAccount.Params, LinkOAuthAccount.Result> {
    
    record Params(
        AuthProvider provider,
        String providerUserId,
        String email,
        String providerName,
        String profileImageUrl,
        String accessToken,
        String refreshToken,
        LocalDateTime tokenExpiry,
        // Dados do perfil (para criação de novo usuário se necessário)
        LocalDate dateOfBirth,
        Gender gender
    ) {
        // Construtor com dados essenciais (Google OAuth)
        public Params(AuthProvider provider, String providerUserId, String email, String providerName, String profileImageUrl) {
            this(provider, providerUserId, email, providerName, profileImageUrl, null, null, null, null, null);
        }
    }
    
    record Result(
        User user,
        String oauthAccountId,
        boolean isNewUser
    ) {}
}