package com.pillmind.presentation.controllers;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pillmind.data.protocols.cryptography.Decrypter;
import com.pillmind.domain.errors.UnauthorizedException;
import com.pillmind.domain.errors.ValidationException;
import com.pillmind.domain.usecases.CreateMedicine;
import com.pillmind.presentation.helpers.HttpHelper;
import com.pillmind.presentation.helpers.TokenExtractor;
import com.pillmind.presentation.protocols.Controller;

import io.javalin.http.Context;

public class CreateMedicineController implements Controller {
    private final CreateMedicine createMedicine;
    private final Decrypter decrypter;
    private final ObjectMapper objectMapper;

    public CreateMedicineController(CreateMedicine createMedicine, Decrypter decrypter, ObjectMapper objectMapper) {
        this.createMedicine = createMedicine;
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

        final MedicineRequest request;
        try {
            request = objectMapper.readValue(ctx.body(), MedicineRequest.class);
        } catch (JsonProcessingException e) {
            throw new ValidationException("Formato JSON inválido na requisição", e);
        }

        if (request.name() == null || request.name().isBlank()) {
            throw new ValidationException("Nome do medicamento é obrigatório");
        }
        if (request.dosage() == null || request.dosage().isBlank()) {
            throw new ValidationException("Dosagem é obrigatória");
        }
        if (request.frequency() == null || request.frequency().isBlank()) {
            throw new ValidationException("Frequência é obrigatória");
        }
        if (request.startDate() == null) {
            throw new ValidationException("Data de início é obrigatória");
        }

        var params = new CreateMedicine.Params(
                userId,
                request.name(),
                request.dosage(),
                request.frequency(),
                request.times() != null ? request.times() : List.of(),
                request.startDate(),
                request.endDate(),
                request.notes(),
                request.imageUrl(),
                request.medicineType(),
                request.prescribedFor(),
                request.quantity(),
                request.reminderOnEmpty());

        var medicine = createMedicine.execute(params);
        HttpHelper.created(ctx, ListMedicinesController.MedicineResponse.from(medicine));
    }

    public record MedicineRequest(
            String name,
            String dosage,
            String frequency,
            List<String> times,
            @JsonFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @JsonFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            String notes,
            String imageUrl,
            String medicineType,
            String prescribedFor,
            Integer quantity,
            Boolean reminderOnEmpty) {}
}
