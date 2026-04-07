package com.pillmind.presentation.controllers;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pillmind.data.protocols.cryptography.Decrypter;
import com.pillmind.domain.errors.UnauthorizedException;
import com.pillmind.domain.errors.ValidationException;
import com.pillmind.domain.models.MedicineDose;
import com.pillmind.domain.usecases.TakeMedicineDose;
import com.pillmind.presentation.helpers.HttpHelper;
import com.pillmind.presentation.helpers.TokenExtractor;
import com.pillmind.presentation.protocols.Controller;

import io.javalin.http.Context;

public class TakeMedicineDoseController implements Controller {
    private final TakeMedicineDose takeMedicineDose;
    private final Decrypter decrypter;
    private final ObjectMapper objectMapper;

    public TakeMedicineDoseController(TakeMedicineDose takeMedicineDose, Decrypter decrypter, ObjectMapper objectMapper) {
        this.takeMedicineDose = takeMedicineDose;
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

        final TakeDoseRequest request;
        try {
            request = objectMapper.readValue(ctx.body(), TakeDoseRequest.class);
        } catch (JsonProcessingException e) {
            throw new ValidationException("Formato JSON inválido na requisição", e);
        }

        if (request.date() == null) {
            throw new ValidationException("Data é obrigatória");
        }
        if (request.scheduledTime() == null || request.scheduledTime().isBlank()) {
            throw new ValidationException("Horário agendado é obrigatório");
        }

        var params = new TakeMedicineDose.Params(userId, medicineId, request.date(), request.scheduledTime(), request.takenAt());
        var dose = takeMedicineDose.execute(params);
        HttpHelper.created(ctx, DoseResponse.from(dose));
    }

    public record TakeDoseRequest(
            @JsonFormat(pattern = "yyyy-MM-dd") LocalDate date,
            String scheduledTime,
            @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime takenAt) {}

    public record DoseResponse(
            String id,
            String medicineId,
            @JsonFormat(pattern = "yyyy-MM-dd") LocalDate date,
            String scheduledTime,
            @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime takenAt,
            boolean skipped,
            @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime createdAt) {

        public static DoseResponse from(MedicineDose d) {
            return new DoseResponse(d.id(), d.medicineId(), d.date(), d.scheduledTime(),
                    d.takenAt(), d.skipped(), d.createdAt());
        }
    }
}
