package com.pillmind.presentation.controllers;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pillmind.data.protocols.cryptography.Decrypter;
import com.pillmind.domain.errors.UnauthorizedException;
import com.pillmind.domain.errors.ValidationException;
import com.pillmind.domain.usecases.SkipMedicineDose;
import com.pillmind.presentation.helpers.HttpHelper;
import com.pillmind.presentation.helpers.TokenExtractor;
import com.pillmind.presentation.protocols.Controller;

import io.javalin.http.Context;

public class SkipMedicineDoseController implements Controller {
    private final SkipMedicineDose skipMedicineDose;
    private final Decrypter decrypter;
    private final ObjectMapper objectMapper;

    public SkipMedicineDoseController(SkipMedicineDose skipMedicineDose, Decrypter decrypter, ObjectMapper objectMapper) {
        this.skipMedicineDose = skipMedicineDose;
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

        String medicineId = ctx.pathParam("id");

        final SkipDoseRequest request;
        try {
            request = objectMapper.readValue(ctx.body(), SkipDoseRequest.class);
        } catch (JsonProcessingException e) {
            throw new ValidationException("Formato JSON inválido na requisição", e);
        }

        if (request.date() == null) {
            throw new ValidationException("Data é obrigatória");
        }
        if (request.scheduledTime() == null || request.scheduledTime().isBlank()) {
            throw new ValidationException("Horário agendado é obrigatório");
        }

        var params = new SkipMedicineDose.Params(userId, medicineId, request.date(), request.scheduledTime());
        var dose = skipMedicineDose.execute(params);
        HttpHelper.created(ctx, TakeMedicineDoseController.DoseResponse.from(dose));
    }

    public record SkipDoseRequest(
            @JsonFormat(pattern = "yyyy-MM-dd") LocalDate date,
            String scheduledTime) {}
}
