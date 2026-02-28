package com.pillmind.data.usecases;

import com.pillmind.data.protocols.db.SocialAccountRepository;
import com.pillmind.domain.models.SocialAccount;
import com.pillmind.domain.usecases.LinkSocialAccount;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Implementação do caso de uso LinkSocialAccount
 */
public class DbLinkSocialAccount extends DbUseCase implements LinkSocialAccount {
    private static final Logger logger = LoggerFactory.getLogger(DbLinkSocialAccount.class);
    
    private final SocialAccountRepository socialAccountRepository;

    public DbLinkSocialAccount(SocialAccountRepository socialAccountRepository) {
        this.socialAccountRepository = socialAccountRepository;
    }

    @Override
    public Result execute(Params params) {
        logger.info("Linking social account for user {} with provider {}", 
                   params.userId(), params.provider());

        // Validar parâmetros
        validateParams(params);

        // Verificar se já existe conta com esse provedor para o usuário
        Optional<SocialAccount> existing = socialAccountRepository
                .loadByUserAndProvider(params.userId(), params.provider());

        if (existing.isPresent()) {
            // Atualizar conta existente
            SocialAccount updated = existing.get()
                    .withUpdatedProfile(params.name(), params.email(), params.profileImageUrl())
                    .withTokens(params.accessToken(), params.refreshToken(), params.tokenExpiry());

            if (params.makePrimary()) {
                socialAccountRepository.setPrimary(updated.id());
                updated = updated.withPrimary(true);
            }

            socialAccountRepository.update(updated);
            
            logger.info("Updated existing social account {} for user {}", 
                       updated.id(), params.userId());
            
            return new Result(updated.id(), false, "Conta social atualizada com sucesso");
        }

        // Verificar se o provider_user_id já está em uso por outro usuário
        Optional<SocialAccount> duplicateProviderAccount = socialAccountRepository
                .loadByProviderAndProviderUserId(params.provider(), params.providerUserId());
        
        if (duplicateProviderAccount.isPresent() && 
            !duplicateProviderAccount.get().userId().equals(params.userId())) {
            throw new IllegalArgumentException(
                String.format("Esta conta %s já está vinculada a outro usuário", params.provider())
            );
        }

        // Criar nova conta social
        SocialAccount newSocialAccount = new SocialAccount(
                params.userId(),
                params.provider(), 
                params.providerUserId(),
                params.email(),
                params.name(),
                params.profileImageUrl(),
                params.accessToken(),
                params.refreshToken(),
                params.tokenExpiry()
        );

        SocialAccount saved = socialAccountRepository.add(newSocialAccount);

        if (params.makePrimary()) {
            socialAccountRepository.setPrimary(saved.id());
        }

        logger.info("Created new social account {} for user {}", saved.id(), params.userId());
        
        return new Result(saved.id(), true, "Conta social vinculada com sucesso");
    }

    private void validateParams(Params params) {
        if (params.userId() == null || params.userId().isBlank()) {
            throw new IllegalArgumentException("User ID é obrigatório");
        }
        if (params.provider() == null || params.provider().isBlank()) {
            throw new IllegalArgumentException("Provider é obrigatório");
        }
        if (params.providerUserId() == null || params.providerUserId().isBlank()) {
            throw new IllegalArgumentException("Provider User ID é obrigatório");
        }
        if (params.email() == null || params.email().isBlank()) {
            throw new IllegalArgumentException("Email é obrigatório");
        }
        if (params.name() == null || params.name().isBlank()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
    }
}