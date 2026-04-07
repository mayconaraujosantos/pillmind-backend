package com.pillmind.presentation.controllers;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pillmind.data.protocols.cryptography.Decrypter;
import com.pillmind.domain.errors.UnauthorizedException;
import com.pillmind.domain.errors.ValidationException;
import com.pillmind.domain.usecases.CreateReminder;
import com.pillmind.presentation.helpers.HttpHelper;
import com.pillmind.presentation.helpers.TokenExtractor;
import com.pillmind.presentation.protocols.Controller;

import io.javalin.http.Context;

public class CreateReminderController implements Controller {
    private final CreateReminder createReminder;
    private final Decrypter decrypter;
    private final ObjectMapper objectMapper;

    public CreateReminderController(CreateReminder createReminder, Decrypter decrypter, ObjectMapper objectMapper) {
        this.createReminder = createReminder;
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

        if (request.medicineId() == null || request.medicineId().isBlank()) {
            throw new ValidationException("medicineId é obrigatório");
        }
        if (request.times() == null || request.times().isEmpty()) {
            throw new ValidationException("times deve conter ao menos um horário");
        }

        var reminder = createReminder.execute(new CreateReminder.Params(
                userId,
                request.medicineId(),
                request.times(),
                request.daysOfWeek() != null ? request.daysOfWeek() : List.of(),
                request.active()));

        HttpHelper.created(ctx, ListRemindersController.ReminderResponse.from(reminder));
    }

    public record ReminderRequest(
            String medicineId,
            List<String> times,
            List<String> daysOfWeek,
            Boolean active) {
    }
}
