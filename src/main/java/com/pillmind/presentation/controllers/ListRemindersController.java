package com.pillmind.presentation.controllers;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pillmind.data.protocols.cryptography.Decrypter;
import com.pillmind.domain.errors.UnauthorizedException;
import com.pillmind.domain.usecases.LoadRemindersByUser;
import com.pillmind.presentation.helpers.HttpHelper;
import com.pillmind.presentation.helpers.TokenExtractor;
import com.pillmind.presentation.protocols.Controller;

import io.javalin.http.Context;

public class ListRemindersController implements Controller {
    private final LoadRemindersByUser loadRemindersByUser;
    private final Decrypter decrypter;

    public ListRemindersController(LoadRemindersByUser loadRemindersByUser, Decrypter decrypter) {
        this.loadRemindersByUser = loadRemindersByUser;
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

        var reminders = loadRemindersByUser.execute(new LoadRemindersByUser.Params(userId));
        HttpHelper.ok(ctx, reminders.stream().map(ReminderResponse::from).toList());
    }

    public record ReminderResponse(
            String id,
            String userId,
            String medicineId,
            List<String> times,
            List<String> daysOfWeek,
            boolean active,
            @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime createdAt,
            @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime updatedAt) {

        public static ReminderResponse from(com.pillmind.domain.models.Reminder r) {
            return new ReminderResponse(r.id(), r.userId(), r.medicineId(), r.times(), r.daysOfWeek(), r.active(),
                    r.createdAt(), r.updatedAt());
        }
    }
}
