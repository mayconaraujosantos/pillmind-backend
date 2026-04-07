package com.pillmind.presentation.controllers;

import com.pillmind.data.protocols.cryptography.Decrypter;
import com.pillmind.domain.errors.UnauthorizedException;
import com.pillmind.domain.usecases.LoadRemindersByMedicine;
import com.pillmind.presentation.helpers.HttpHelper;
import com.pillmind.presentation.helpers.TokenExtractor;
import com.pillmind.presentation.protocols.Controller;

import io.javalin.http.Context;

public class ListRemindersByMedicineController implements Controller {
    private final LoadRemindersByMedicine loadRemindersByMedicine;
    private final Decrypter decrypter;

    public ListRemindersByMedicineController(LoadRemindersByMedicine loadRemindersByMedicine, Decrypter decrypter) {
        this.loadRemindersByMedicine = loadRemindersByMedicine;
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

        String medicineId = ctx.pathParam("medicineId");
        var reminders = loadRemindersByMedicine.execute(new LoadRemindersByMedicine.Params(userId, medicineId));

        HttpHelper.ok(ctx, reminders.stream().map(ListRemindersController.ReminderResponse::from).toList());
    }
}
