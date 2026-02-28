package com.pillmind.domain.usecases;

import com.pillmind.domain.models.User;

/**
 * Use Case para carregar usuário por ID
 */
public interface LoadUserById extends UseCase<LoadUserById.Params, User> {
    
    record Params(String userId) {}
}