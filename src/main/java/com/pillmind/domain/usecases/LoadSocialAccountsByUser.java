package com.pillmind.domain.usecases;

import com.pillmind.domain.models.SocialAccount;
import java.util.List;

/**
 * Caso de uso: Listar contas sociais de um usuário
 */
public interface LoadSocialAccountsByUser extends UseCase<LoadSocialAccountsByUser.Params, LoadSocialAccountsByUser.Result> {
    record Params(String userId) {}

    record Result(
        List<SocialAccount> socialAccounts,
        SocialAccount primaryAccount
    ) {}
}