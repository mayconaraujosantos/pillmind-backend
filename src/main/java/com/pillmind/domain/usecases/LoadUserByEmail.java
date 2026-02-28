package com.pillmind.domain.usecases;

import com.pillmind.domain.models.User;

/**
 * Use Case para carregar usuário por email
 */
public interface LoadUserByEmail extends UseCase<LoadUserByEmail.Params, User> {
    
    record Params(String email) {}
}