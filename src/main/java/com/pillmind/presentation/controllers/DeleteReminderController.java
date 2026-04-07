package com.pillmind.presentation.controllers;

import com.pillmind.data.protocols.cryptography.Decrypter;
import com.pillmind.domain.errors.UnauthorizedException;
import com.pillmind.domain.usecases.DeleteReminder;
import com.pillmind.presentation.helpers.HttpHelper;
import com.pillmind.presentation.helpers.TokenExtractor;
import com.pillmind.presentation.protocols.Controller;

import io.javalin.http.Context;

public class DeleteReminderController implements Controller {
    private final DeleteReminder deleteReminder;
    private final Decrypter decrypter;

    public DeleteReminderController(DeleteReminder deleteReminder, Decrypter decrypter) {
        this.deleteReminder = deleteReminder;
        this.decrypter = decrypter;
    }

    @Override
    public void handle(Context ctx) {
        String token = TokenExtractor.extractAccessToken(ctx);
        if (token == null || token.isBlank()) {
            throw new UnauthorizedException("Token de acesso ausente");
        }

        final String userId;
        try {
            userId = decrypter.decrypt(token);
        } catch (Exception e) {
            throw new UnauthorizedException("Token inválido", e);
        }

        deleteReminder.execute(new DeleteReminder.Params(ctx.pathParam("id"), userId));
        HttpHelper.noContent(ctx);
    }
}
