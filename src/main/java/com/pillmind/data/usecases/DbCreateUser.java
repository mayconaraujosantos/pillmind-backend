package com.pillmind.data.usecases;

import java.util.UUID;

import com.pillmind.data.protocols.db.UserRepository;
import com.pillmind.domain.errors.ConflictException;
import com.pillmind.domain.models.User;
import com.pillmind.domain.usecases.CreateUser;

/**
 * Implementação do caso de uso CreateUser
 */
public class DbCreateUser extends DbUseCase implements CreateUser {
    private final UserRepository userRepository;

    public DbCreateUser(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User execute(Params params) {
        // 1. Verificar se email já está em uso
        if (userRepository.emailExists(params.email())) {
            throw new ConflictException("Este email já está em uso. Use outro email.");
        }

        // 2. Criar novo usuário
        var user = new User(
            UUID.randomUUID().toString(),
            params.name(),
            params.email(),
            params.dateOfBirth(),
            params.gender(),
            params.pictureUrl()
        );

        // 3. Persistir no banco
        return userRepository.add(user);
    }
}