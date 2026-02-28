package com.pillmind.data.usecases;

import com.pillmind.data.protocols.db.UserRepository;
import com.pillmind.domain.errors.NotFoundException;
import com.pillmind.domain.models.User;
import com.pillmind.domain.usecases.LoadUserByEmail;

/**
 * Implementação do caso de uso LoadUserByEmail
 */
public class DbLoadUserByEmail extends DbUseCase implements LoadUserByEmail {
    private final UserRepository userRepository;

    public DbLoadUserByEmail(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User execute(Params params) {
        return userRepository.findByEmail(params.email())
            .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
    }
}