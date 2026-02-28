package com.pillmind.data.protocols.db;

import java.util.Optional;

import com.pillmind.domain.models.User;

/**
 * Interface para operações de usuário no banco de dados
 */
public interface UserRepository {
    
    /**
     * Adiciona um novo usuário
     */
    User add(User user);
    
    /**
     * Atualiza dados de um usuário existente
     */
    User update(User user);
    
    /**
     * Busca usuário por ID
     */
    Optional<User> findById(String id);
    
    /**
     * Busca usuário por email
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Verifica se email já está em uso
     */
    boolean emailExists(String email);
    
    /**
     * Deleta usuário por ID (soft delete ou hard delete conforme implementação)
     */
    boolean delete(String id);
}