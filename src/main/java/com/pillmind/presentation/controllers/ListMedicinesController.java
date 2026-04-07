package com.pillmind.presentation.controllers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pillmind.data.protocols.cryptography.Decrypter;
import com.pillmind.domain.errors.UnauthorizedException;
import com.pillmind.domain.usecases.LoadMedicinesByUser;
import com.pillmind.presentation.helpers.HttpHelper;
import com.pillmind.presentation.helpers.TokenExtractor;
import com.pillmind.presentation.protocols.Controller;

import io.javalin.http.Context;

public class ListMedicinesController implements Controller {
    private final LoadMedicinesByUser loadMedicinesByUser;
    private final Decrypter decrypter;

    public ListMedicinesController(LoadMedicinesByUser loadMedicinesByUser, Decrypter decrypter) {
        this.loadMedicinesByUser = loadMedicinesByUser;
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

        var medicines = loadMedicinesByUser.execute(new LoadMedicinesByUser.Params(userId));
        HttpHelper.ok(ctx, medicines.stream().map(MedicineResponse::from).toList());
    }

    public record MedicineResponse(
            String id,
            String userId,
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
            Boolean reminderOnEmpty,
            @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime createdAt,
            @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime updatedAt) {

        public static MedicineResponse from(com.pillmind.domain.models.Medicine m) {
            return new MedicineResponse(m.id(), m.userId(), m.name(), m.dosage(), m.frequency(),
                    m.times(), m.startDate(), m.endDate(), m.notes(), m.imageUrl(),
                    m.medicineType(), m.prescribedFor(), m.quantity(), m.reminderOnEmpty(),
                    m.createdAt(), m.updatedAt());
        }
    }
}
