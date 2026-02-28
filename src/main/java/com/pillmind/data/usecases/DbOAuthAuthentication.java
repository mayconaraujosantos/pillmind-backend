package com.pillmind.data.usecases;

import java.time.LocalDateTime;

import com.pillmind.data.protocols.cryptography.Encrypter;
import com.pillmind.data.protocols.db.OAuthAccountRepository;
import com.pillmind.data.protocols.db.UserRepository;
import com.pillmind.domain.errors.UnauthorizedException;
import com.pillmind.domain.usecases.OAuthAuthentication;

/**
 * Implementação do caso de uso OAuthAuthentication
 */
public class DbOAuthAuthentication extends DbUseCase implements OAuthAuthentication {
    private final OAuthAccountRepository oauthAccountRepository;
    private final UserRepository userRepository;
    private final Encrypter encrypter;

    public DbOAuthAuthentication(OAuthAccountRepository oauthAccountRepository, UserRepository userRepository, Encrypter encrypter) {
        this.oauthAccountRepository = oauthAccountRepository;
        this.userRepository = userRepository;
        this.encrypter = encrypter;
    }

    @Override
    public Result execute(Params params) {
        // 1. Buscar conta OAuth por provedor e providerUserId
        var oauthAccount = oauthAccountRepository.findByProviderAndProviderUserId(
            params.provider(), params.providerUserId())
            .orElseThrow(() -> new UnauthorizedException("Conta OAuth não encontrada"));

        // 2. Buscar dados do usuário (perfil)
        var user = userRepository.findById(oauthAccount.userId())
            .orElseThrow(() -> new UnauthorizedException("Usuário não encontrado"));

        // 3. Atualizar timestamp de último login
        var updatedOAuthAccount = oauthAccount.withLastLoginAt(LocalDateTime.now());
        oauthAccountRepository.update(updatedOAuthAccount);

        // 4. Gerar token de acesso
        var accessToken = encrypter.encrypt(user.id());

        return new Result(accessToken, user);
    }
}