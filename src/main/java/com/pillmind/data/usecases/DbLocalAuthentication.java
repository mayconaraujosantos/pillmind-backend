package com.pillmind.data.usecases;

import java.time.LocalDateTime;

import com.pillmind.data.protocols.cryptography.Encrypter;
import com.pillmind.data.protocols.cryptography.HashComparer;
import com.pillmind.data.protocols.db.LocalAccountRepository;
import com.pillmind.data.protocols.db.UserRepository;
import com.pillmind.domain.errors.UnauthorizedException;
import com.pillmind.domain.usecases.LocalAuthentication;

/**
 * Implementação do caso de uso LocalAuthentication
 */
public class DbLocalAuthentication extends DbUseCase implements LocalAuthentication {
    private final LocalAccountRepository localAccountRepository;
    private final UserRepository userRepository;
    private final HashComparer hashComparer;
    private final Encrypter encrypter;

    public DbLocalAuthentication(LocalAccountRepository localAccountRepository, UserRepository userRepository, 
                                HashComparer hashComparer, Encrypter encrypter) {
        this.localAccountRepository = localAccountRepository;
        this.userRepository = userRepository;
        this.hashComparer = hashComparer;
        this.encrypter = encrypter;
    }

    @Override
    public Result execute(Params params) {
        // 1. Buscar conta local por email
        var localAccount = localAccountRepository.findByEmail(params.email())
            .orElseThrow(() -> new UnauthorizedException("Credenciais inválidas"));

        // 2. Comparar senha
        if (!hashComparer.compare(params.password(), localAccount.passwordHash())) {
            throw new UnauthorizedException("Credenciais inválidas");
        }

        // 3. Buscar dados do usuário (perfil)
        var user = userRepository.findById(localAccount.userId())
            .orElseThrow(() -> new UnauthorizedException("Usuário não encontrado"));

        // 4. Atualizar timestamp de último login
        var updatedLocalAccount = localAccount.withLastLoginAt(LocalDateTime.now());
        localAccountRepository.update(updatedLocalAccount);

        // 5. Gerar token de acesso
        var accessToken = encrypter.encrypt(user.id());

        return new Result(accessToken, user);
    }
}