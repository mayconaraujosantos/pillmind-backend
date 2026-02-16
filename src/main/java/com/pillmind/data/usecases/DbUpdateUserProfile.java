package com.pillmind.data.usecases;

import com.pillmind.data.protocols.db.UserRepository;
import com.pillmind.domain.errors.ConflictException;
import com.pillmind.domain.errors.NotFoundException;
import com.pillmind.domain.models.User;
import com.pillmind.domain.usecases.UpdateUserProfile;

/**
 * Implementação do caso de uso UpdateUserProfile
 */
public class DbUpdateUserProfile extends DbUseCase implements UpdateUserProfile {
    private final UserRepository userRepository;

    public DbUpdateUserProfile(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User execute(Params params) {
        // 1. Verificar se usuário existe
        var existingUser = userRepository.findById(params.userId())
            .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

        // 2. Se email for alterado, verificar se não está em uso por outro usuário
        if (!existingUser.email().equals(params.email())) {
            var userWithSameEmail = userRepository.findByEmail(params.email());
            if (userWithSameEmail.isPresent() && !userWithSameEmail.get().id().equals(params.userId())) {
                throw new ConflictException("Este email já está em uso por outro usuário.");
            }
        }

        // 3. Criar usuário atualizado
        var updatedUser = existingUser.withUpdatedProfile(
            params.name(),
            params.email(),
            params.dateOfBirth(),
            params.gender(),
            params.pictureUrl()
        );

        // 4. Persistir atualização
        return userRepository.update(updatedUser);
    }
}