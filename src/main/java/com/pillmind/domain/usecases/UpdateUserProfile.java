package com.pillmind.domain.usecases;

import java.time.LocalDate;

import com.pillmind.domain.models.Gender;
import com.pillmind.domain.models.User;

/**
 * Use Case para atualizar perfil do usuário
 */
public interface UpdateUserProfile extends UseCase<UpdateUserProfile.Params, User> {
    
    record Params(
        String userId,
        String name,
        String email,
        LocalDate dateOfBirth,
        Gender gender,
        String pictureUrl
    ) {}
}