package com.pillmind.domain.usecases;

import com.pillmind.domain.models.User;

/**
 * Use Case para autenticação local (signin com email/senha)
 */
public interface LocalAuthentication extends UseCase<LocalAuthentication.Params, LocalAuthentication.Result> {
    
    record Params(
        String email,
        String password
    ) {}
    
    record Result(
        String accessToken,
        User user
    ) {}
}