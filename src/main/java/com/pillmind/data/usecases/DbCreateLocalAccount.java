package com.pillmind.data.usecases;

import java.util.UUID;

import com.pillmind.data.protocols.cryptography.Hasher;
import com.pillmind.data.protocols.db.LocalAccountRepository;
import com.pillmind.data.protocols.db.UserRepository;
import com.pillmind.domain.errors.ConflictException;
import com.pillmind.domain.models.LocalAccount;
import com.pillmind.domain.models.User;
import com.pillmind.domain.usecases.CreateLocalAccount;

/**
 * Implementação do caso de uso CreateLocalAccount
 * Cria tanto o User (perfil) quanto o LocalAccount (credencial)
 */
public class DbCreateLocalAccount extends DbUseCase implements CreateLocalAccount {
    private final Hasher hasher;
    private final UserRepository userRepository;
    private final LocalAccountRepository localAccountRepository;

    public DbCreateLocalAccount(Hasher hasher, UserRepository userRepository, LocalAccountRepository localAccountRepository) {
        this.hasher = hasher;
        this.userRepository = userRepository;
        this.localAccountRepository = localAccountRepository;
    }

    @Override
    public Result execute(Params params) {
        // 1. Verificar se email já está em uso (tanto em users quanto em local_accounts)
        if (userRepository.emailExists(params.email())) {
            throw new ConflictException("Este email já está em uso. Use outro email ou faça login.");
        }
        
        if (localAccountRepository.emailExists(params.email())) {
            throw new ConflictException("Este email já está em uso. Use outro email ou faça login.");
        }

        // 2. Hash da senha
        var hashedPassword = hasher.hash(params.password());

        // 3. Criar usuário (perfil)
        var userId = UUID.randomUUID().toString();
        var user = new User(
            userId,
            params.name(),
            params.email(),
            params.dateOfBirth(),
            params.gender(),
            params.pictureUrl()
        );

        // 4. Criar conta local (credencial)
        var localAccountId = UUID.randomUUID().toString();
        var localAccount = new LocalAccount(
            localAccountId,
            userId,
            params.email(),
            hashedPassword
        );

        // 5. Persistir ambos (ordem importante: primeiro user, depois local_account)
        var createdUser = userRepository.add(user);
        localAccountRepository.add(localAccount);

        return new Result(createdUser, localAccountId);
    }
}