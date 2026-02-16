package com.pillmind.data.usecases;

import java.time.LocalDateTime;
import java.util.UUID;

import com.pillmind.data.protocols.db.OAuthAccountRepository;
import com.pillmind.data.protocols.db.UserRepository;
import com.pillmind.domain.models.OAuthAccount;
import com.pillmind.domain.models.User;
import com.pillmind.domain.usecases.LinkOAuthAccount;

/**
 * Implementação do caso de uso LinkOAuthAccount
 * Se usuário não existir, cria User + OAuthAccount
 * Se existir, apenas vincula novo OAuthAccount ou atualiza existente
 */
public class DbLinkOAuthAccount extends DbUseCase implements LinkOAuthAccount {
    private final UserRepository userRepository;
    private final OAuthAccountRepository oauthAccountRepository;

    public DbLinkOAuthAccount(UserRepository userRepository, OAuthAccountRepository oauthAccountRepository) {
        this.userRepository = userRepository;
        this.oauthAccountRepository = oauthAccountRepository;
    }

    @Override
    public Result execute(Params params) {
        // 1. Verificar se já existe conta OAuth do mesmo provedor
        var existingOAuthAccount = oauthAccountRepository.findByProviderAndProviderUserId(
            params.provider(), params.providerUserId());

        if (existingOAuthAccount.isPresent()) {
            // OAuth account já existe, atualizar dados e retornar o usuário
            var oauthAccount = existingOAuthAccount.get();
            var updatedOAuthAccount = oauthAccount.withUpdatedProviderData(
                params.providerName(),
                params.email(),
                params.profileImageUrl()
            ).withLastLoginAt(LocalDateTime.now());
            
            // Atualizar tokens se fornecidos
            if (params.accessToken() != null) {
                updatedOAuthAccount = updatedOAuthAccount.withUpdatedTokens(
                    params.accessToken(), 
                    params.refreshToken(), 
                    params.tokenExpiry()
                );
            }
            
            oauthAccountRepository.update(updatedOAuthAccount);
            
            var user = userRepository.findById(oauthAccount.userId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
            
            // Atualizar dados do usuário se mudou (nome, foto)
            var updatedUser = user.withUpdatedProfile(params.providerName(), params.profileImageUrl());
            if (!user.equals(updatedUser)) {
                userRepository.update(updatedUser);
            }
            
            return new Result(updatedUser, oauthAccount.id(), false);
        }

        // 2. Tentar encontrar usuário existente por email
        var existingUser = userRepository.findByEmail(params.email());
        
        User user;
        boolean isNewUser;
        
        if (existingUser.isPresent()) {
            // Usuário existe, apenas vincular nova conta OAuth
            user = existingUser.get();
            isNewUser = false;
            
            // Atualizar dados do usuário se mudou (nome, foto)
            var updatedUser = user.withUpdatedProfile(params.providerName(), params.profileImageUrl());
            if (!user.equals(updatedUser)) {
                user = userRepository.update(updatedUser);
            }
        } else {
            // Criar novo usuário
            var userId = UUID.randomUUID().toString();
            user = new User(
                userId,
                params.providerName(),
                params.email(),
                params.dateOfBirth(),
                params.gender(),
                params.profileImageUrl()
            );
            user = userRepository.add(user);
            isNewUser = true;
        }

        // 3. Marcar outras contas OAuth deste usuário como não primárias
        oauthAccountRepository.clearPrimaryByUserId(user.id());

        // 4. Criar nova conta OAuth como primária
        var oauthAccountId = UUID.randomUUID().toString();
        var oauthAccount = new OAuthAccount(
            oauthAccountId,
            user.id(),
            params.provider(),
            params.providerUserId(),
            params.email(),
            params.providerName(),
            params.profileImageUrl(),
            params.accessToken(),
            params.refreshToken(),
            params.tokenExpiry()
        );
        
        oauthAccountRepository.add(oauthAccount);

        return new Result(user, oauthAccountId, isNewUser);
    }
}