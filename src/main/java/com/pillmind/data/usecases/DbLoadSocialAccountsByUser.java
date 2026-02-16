package com.pillmind.data.usecases;

import com.pillmind.data.protocols.db.SocialAccountRepository;
import com.pillmind.domain.models.SocialAccount;
import com.pillmind.domain.usecases.LoadSocialAccountsByUser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Implementação do caso de uso LoadSocialAccountsByUser
 */
public class DbLoadSocialAccountsByUser extends DbUseCase implements LoadSocialAccountsByUser {
    private static final Logger logger = LoggerFactory.getLogger(DbLoadSocialAccountsByUser.class);
    
    private final SocialAccountRepository socialAccountRepository;

    public DbLoadSocialAccountsByUser(SocialAccountRepository socialAccountRepository) {
        this.socialAccountRepository = socialAccountRepository;
    }

    @Override
    public Result execute(Params params) {
        logger.debug("Loading social accounts for user {}", params.userId());

        if (params.userId() == null || params.userId().isBlank()) {
            throw new IllegalArgumentException("User ID é obrigatório");
        }

        List<SocialAccount> socialAccounts = socialAccountRepository.loadByUserId(params.userId());
        SocialAccount primaryAccount = socialAccountRepository.loadPrimaryByUserId(params.userId())
                .orElse(null);

        logger.debug("Found {} social accounts for user {}, primary: {}", 
                    socialAccounts.size(), params.userId(), 
                    primaryAccount != null ? primaryAccount.provider() : "none");

        return new Result(socialAccounts, primaryAccount);
    }
}