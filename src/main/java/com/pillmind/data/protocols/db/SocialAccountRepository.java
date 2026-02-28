package com.pillmind.data.protocols.db;

import java.util.List;
import java.util.Optional;

import com.pillmind.domain.models.SocialAccount;

/**
 * Protocolo para operações com contas sociais no repositório
 */
public interface SocialAccountRepository {
    
    /**
     * Adiciona uma nova conta social
     */
    SocialAccount add(SocialAccount socialAccount);

    /**
     * Atualiza uma conta social existente
     */
    SocialAccount update(SocialAccount socialAccount);

    /**
     * Busca conta social por ID
     */
    Optional<SocialAccount> loadById(String id);

    /**
     * Busca conta social por usuário e provedor
     */
    Optional<SocialAccount> loadByUserAndProvider(String userId, String provider);

    /**
     * Busca conta social por provedor e ID do usuário no provedor
     */
    Optional<SocialAccount> loadByProviderAndProviderUserId(String provider, String providerUserId);

    /**
     * Lista todas as contas sociais de um usuário
     */
    List<SocialAccount> loadByUserId(String userId);

    /**
     * Busca a conta social primária de um usuário
     */
    Optional<SocialAccount> loadPrimaryByUserId(String userId);

    /**
     * Define uma conta social como primária (remove primary das outras)
     */
    void setPrimary(String socialAccountId);

    /**
     * Remove uma conta social
     */
    void delete(String id);

    /**
     * Remove todas as contas sociais de um usuário
     */
    void deleteByUserId(String userId);

    /**
     * Verifica se um usuário já possui conta com o provedor especificado
     */
    boolean existsByUserAndProvider(String userId, String provider);

    /**
     * Lista contas sociais por provedor
     */
    List<SocialAccount> loadByProvider(String provider);
}