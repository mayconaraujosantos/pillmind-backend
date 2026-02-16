package com.pillmind.domain.usecases;

import java.time.LocalDate;

import com.pillmind.domain.models.Gender;
import com.pillmind.domain.models.User;

/**
 * Use Case para criar conta local (signup com email/senha)
 * Cria tanto o User (perfil) quanto o LocalAccount (credencial)
 */
public interface CreateLocalAccount extends UseCase<CreateLocalAccount.Params, CreateLocalAccount.Result> {
    
    record Params(
        String name,
        String email,
        String password,
        LocalDate dateOfBirth,
        Gender gender,
        String pictureUrl
    ) {
        // Construtor apenas com dados essenciais
        public Params(String name, String email, String password) {
            this(name, email, password, null, null, null);
        }
    }
    
    record Result(
        User user,
        String localAccountId
    ) {}
}