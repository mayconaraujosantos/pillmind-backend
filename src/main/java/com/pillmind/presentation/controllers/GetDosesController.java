package com.pillmind.presentation.controllers;

import java.time.LocalDate;

import com.pillmind.data.protocols.cryptography.Decrypter;
import com.pillmind.domain.errors.UnauthorizedException;
import com.pillmind.domain.errors.ValidationException;
import com.pillmind.domain.usecases.LoadDosesByDate;
import com.pillmind.domain.usecases.LoadDosesByMedicineAndDate;
import com.pillmind.presentation.helpers.HttpHelper;
import com.pillmind.presentation.helpers.TokenExtractor;
import com.pillmind.presentation.protocols.Controller;

import io.javalin.http.Context;

/**
 * Handles GET /api/medicines/doses/today and GET /api/medicines/doses?date=
 * Also handles GET /api/medicines/{id}/doses?date=
 */
public class GetDosesController implements Controller {
    private final LoadDosesByDate loadDosesByDate;
    private final LoadDosesByMedicineAndDate loadDosesByMedicineAndDate;
    private final Decrypter decrypter;

    public GetDosesController(
            LoadDosesByDate loadDosesByDate,
            LoadDosesByMedicineAndDate loadDosesByMedicineAndDate,
            Decrypter decrypter) {
        this.loadDosesByDate = loadDosesByDate;
        this.loadDosesByMedicineAndDate = loadDosesByMedicineAndDate;
        this.decrypter = decrypter;
    }

    public void handleByDate(Context ctx) {
        String userId = resolveUserId(ctx);
        LocalDate date = resolveDate(ctx);
        var doses = loadDosesByDate.execute(new LoadDosesByDate.Params(userId, date));
        HttpHelper.ok(ctx, doses.stream().map(TakeMedicineDoseController.DoseResponse::from).toList());
    }

    public void handleByMedicineAndDate(Context ctx) {
        String userId = resolveUserId(ctx);
        String medicineId = ctx.pathParam("id");
        LocalDate date = resolveDate(ctx);
        var doses = loadDosesByMedicineAndDate.execute(new LoadDosesByMedicineAndDate.Params(medicineId, userId, date));
        HttpHelper.ok(ctx, doses.stream().map(TakeMedicineDoseController.DoseResponse::from).toList());
    }

    @Override
    public void handle(Context ctx) {
        handleByDate(ctx);
    }

    private String resolveUserId(Context ctx) {
        String token = TokenExtractor.extractAccessToken(ctx);
        if (token == null || token.isBlank()) {
            throw new UnauthorizedException("Token de acesso ausente");
        }
        try {
            return decrypter.decrypt(token);
        } catch (Exception e) {
            throw new UnauthorizedException("Token inválido", e);
        }
    }

    private LocalDate resolveDate(Context ctx) {
        String dateParam = ctx.queryParam("date");
        if (dateParam != null && !dateParam.isBlank()) {
            try {
                return LocalDate.parse(dateParam);
            } catch (Exception e) {
                throw new ValidationException("Formato de data inválido. Use yyyy-MM-dd");
            }
        }
        return LocalDate.now();
    }
}
