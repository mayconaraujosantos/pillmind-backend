package com.pillmind.domain.usecases;

/**
 * Caso de uso: Vincular uma conta social a um usuário
 */
public interface LinkSocialAccount extends UseCase<LinkSocialAccount.Params, LinkSocialAccount.Result> {
    record Params(
        String userId,
        String provider,
        String providerUserId,
        String email,
        String name,
        String profileImageUrl,
        String accessToken,
        String refreshToken,
        java.time.LocalDateTime tokenExpiry,
        boolean makePrimary
    ) {}

    record Result(
        String socialAccountId,
        boolean isNewLink,
        String message
    ) {}
}
