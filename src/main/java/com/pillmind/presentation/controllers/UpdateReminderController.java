package com.pillmind.presentation.controllers;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pillmind.data.protocols.cryptography.Decrypter;
import com.pillmind.domain.errors.UnauthorizedException;
import com.pillmind.domain.errors.ValidationException;
import com.pillmind.domain.usecases.UpdateReminder;
import com.pillmind.presentation.helpers.HttpHelper;
import com.pillmind.presentation.helpers.TokenExtractor;
import com.pillmind.presentation.protocols.Controller;

import io.javalin.http.Context;

public class UpdateReminderController implements Controller {
    private final UpdateReminder updateReminder;
    private final Decrypter decrypter;
    private final ObjectMapper objectMapper;

    public UpdateReminderController(UpdateReminder updateReminder, Decrypter decrypter, ObjectMapper objectMapper) {
        this.updateReminder = updateReminder;
        this.decrypter = decrypter;
        this.objectMapper = objectMapper;
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

        final ReminderRequest request;
        try {
            request = objectMapper.readValue(ctx.body(), ReminderRequest.class);
        } catch (JsonProcessingException e) {
            throw new ValidationException("Formato JSON inválido na requisição", e);
        }

        // times must have at least one entry when provided (empty times = nothing to remind)
        if (request.times() != null && request.times().isEmpty()) {
            throw new ValidationException("times não pode ser vazio quando informado");
        }
        // daysOfWeek: empty list is valid — it means "every day", so no rejection here

        var reminder = updateReminder.execute(new UpdateReminder.Params(
                ctx.pathParam("id"),
                userId,
                request.times(),
                request.daysOfWeek(),
                request.active()));

        HttpHelper.ok(ctx, ListRemindersController.ReminderResponse.from(reminder));
    }

    public record ReminderRequest(
            List<String> times,
            List<String> daysOfWeek,
            Boolean active) {
    }
}
