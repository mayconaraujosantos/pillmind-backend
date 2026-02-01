package com.pillmind.presentation.controllers;

import com.pillmind.data.protocols.cryptography.Decrypter;
import com.pillmind.domain.errors.UnauthorizedException;
import com.pillmind.domain.usecases.LoadAccountById;
import com.pillmind.presentation.helpers.HttpHelper;
import com.pillmind.presentation.protocols.Controller;

import io.javalin.http.Context;

/**
 * Controller para obter perfil do usuário autenticado
 */
public class ProfileController implements Controller {
  private final LoadAccountById loadAccountById;
  private final Decrypter decrypter;

  public ProfileController(LoadAccountById loadAccountById, Decrypter decrypter) {
    this.loadAccountById = loadAccountById;
    this.decrypter = decrypter;
  }

  @Override
  public void handle(Context ctx) {
    String token = ctx.header("x-access-token");
    if (token == null || token.isBlank()) {
      throw new UnauthorizedException("Token de acesso ausente");
    }

    final String accountId;
    try {
      accountId = decrypter.decrypt(token);
    } catch (Exception e) {
      throw new UnauthorizedException("Token inválido", e);
    }

    var account = loadAccountById.execute(new LoadAccountById.Params(accountId));

    HttpHelper.ok(ctx, new ProfileResponse(
        account.id(),
        account.name(),
        account.email(),
        account.pictureUrl(),
        account.updatedAt()));
  }

  public record ProfileResponse(
      String id,
      String name,
      String email,
      String pictureUrl,
      java.time.LocalDateTime updatedAt) {
  }
}
