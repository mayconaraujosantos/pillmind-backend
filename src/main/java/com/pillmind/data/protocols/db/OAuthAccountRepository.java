package com.pillmind.data.protocols.db;

import java.util.List;
import java.util.Optional;

import com.pillmind.domain.models.AuthProvider;
import com.pillmind.domain.models.OAuthAccount;

/**
 * Interface para operações de conta OAuth2 no banco de dados
 */
public interface OAuthAccountRepository {
    
    /**
     * Adiciona uma nova conta OAuth2
     */
    OAuthAccount add(OAuthAccount oauthAccount);
    
    /**
     * Atualiza dados de uma conta OAuth2 existente
     */
    OAuthAccount update(OAuthAccount oauthAccount);
    
    /**
     * Busca conta OAuth2 por ID
     */
    Optional<OAuthAccount> findById(String id);
    
    /**
     * Busca conta OAuth2 por provedor e ID do provedor
     */
    Optional<OAuthAccount> findByProviderAndProviderUserId(AuthProvider provider, String providerUserId);
    
    /**
     * Busca todas as contas OAuth2 de um usuário
     */
    List<OAuthAccount> findByUserId(String userId);
    
    /**
     * Busca conta OAuth2 primária de um usuário
     */
    Optional<OAuthAccount> findPrimaryByUserId(String userId);
    
    /**
     * Busca contas OAuth2 de um usuário por provedor
     */
    List<OAuthAccount> findByUserIdAndProvider(String userId, AuthProvider provider);
    
    /**
     * Define todas as contas de um usuário como não primárias
     */
    void clearPrimaryByUserId(String userId);
    
    /**
     * Deleta conta OAuth2 por ID
     */
    boolean delete(String id);
    
    /**
     * Deleta todas as contas OAuth2 de um usuário
     */
    boolean deleteByUserId(String userId);
    
    /**
     * Conta quantas contas OAuth2 um usuário tem
     */
    long countByUserId(String userId);
}