package com.pillmind.presentation.controllers;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pillmind.data.protocols.cryptography.Decrypter;
import com.pillmind.domain.errors.UnauthorizedException;
import com.pillmind.domain.errors.ValidationException;
import com.pillmind.domain.usecases.UpdateMedicine;
import com.pillmind.presentation.helpers.HttpHelper;
import com.pillmind.presentation.helpers.TokenExtractor;
import com.pillmind.presentation.protocols.Controller;

import io.javalin.http.Context;

public class UpdateMedicineController implements Controller {
    private final UpdateMedicine updateMedicine;
    private final Decrypter decrypter;
    private final ObjectMapper objectMapper;

    public UpdateMedicineController(UpdateMedicine updateMedicine, Decrypter decrypter, ObjectMapper objectMapper) {
        this.updateMedicine = updateMedicine;
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

        final CreateMedicineController.MedicineRequest request;
        try {
            request = objectMapper.readValue(ctx.body(), CreateMedicineController.MedicineRequest.class);
        } catch (JsonProcessingException e) {
            throw new ValidationException("Formato JSON inválido na requisição", e);
        }

        if (request.name() == null || request.name().isBlank()) {
            throw new ValidationException("Nome do medicamento é obrigatório");
        }
        if (request.startDate() == null) {
            throw new ValidationException("Data de início é obrigatória");
        }

        var params = new UpdateMedicine.Params(
                medicineId,
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

        var medicine = updateMedicine.execute(params);
        HttpHelper.ok(ctx, ListMedicinesController.MedicineResponse.from(medicine));
    }
}
