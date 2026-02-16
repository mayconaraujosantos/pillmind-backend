package com.pillmind.data.protocols.db;

import java.util.Optional;

import com.pillmind.domain.models.LocalAccount;

/**
 * Interface para operações de conta local no banco de dados
 */
public interface LocalAccountRepository {
    
    /**
     * Adiciona uma nova conta local
     */
    LocalAccount add(LocalAccount localAccount);
    
    /**
     * Atualiza dados de uma conta local existente
     */
    LocalAccount update(LocalAccount localAccount);
    
    /**
     * Busca conta local por ID
     */
    Optional<LocalAccount> findById(String id);
    
    /**
     * Busca conta local por email
     */
    Optional<LocalAccount> findByEmail(String email);
    
    /**
     * Busca conta local por ID do usuário
     */
    Optional<LocalAccount> findByUserId(String userId);
    
    /**
     * Verifica se email já está em uso por uma conta local
     */
    boolean emailExists(String email);
    
    /**
     * Deleta conta local por ID
     */
    boolean delete(String id);
    
    /**
     * Deleta todas as contas locais de um usuário
     */
    boolean deleteByUserId(String userId);
}