package com.pillmind.domain.usecases;

import java.time.LocalDate;

import com.pillmind.domain.models.Gender;
import com.pillmind.domain.models.User;

/**
 * Use Case para criar um novo usuário (perfil)
 */
public interface CreateUser extends UseCase<CreateUser.Params, User> {
    
    record Params(
        String name,
        String email,
        LocalDate dateOfBirth,
        Gender gender,
        String pictureUrl
    ) {
        // Construtor apenas com dados essenciais
        public Params(String name, String email) {
            this(name, email, null, null, null);
        }
    }
}