package com.pillmind.data.usecases;

import com.pillmind.data.protocols.db.UserRepository;
import com.pillmind.domain.errors.NotFoundException;
import com.pillmind.domain.models.User;
import com.pillmind.domain.usecases.LoadUserById;

/**
 * Implementação do caso de uso LoadUserById
 */
public class DbLoadUserById extends DbUseCase implements LoadUserById {
    private final UserRepository userRepository;

    public DbLoadUserById(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User execute(Params params) {
        return userRepository.findById(params.userId())
            .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
    }
}