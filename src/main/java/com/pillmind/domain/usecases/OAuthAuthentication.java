package com.pillmind.domain.usecases;

import com.pillmind.domain.models.AuthProvider;
import com.pillmind.domain.models.User;

/**
 * Use Case para autenticação OAuth2
 */
public interface OAuthAuthentication extends UseCase<OAuthAuthentication.Params, OAuthAuthentication.Result> {
    
    record Params(
        AuthProvider provider,
        String providerUserId
    ) {}
    
    record Result(
        String accessToken,
        User user
    ) {}
}