package com.pillmind.presentation.controllers;

import com.pillmind.data.protocols.cryptography.Decrypter;
import com.pillmind.domain.errors.UnauthorizedException;
import com.pillmind.domain.usecases.DeleteMedicine;
import com.pillmind.presentation.helpers.HttpHelper;
import com.pillmind.presentation.helpers.TokenExtractor;
import com.pillmind.presentation.protocols.Controller;

import io.javalin.http.Context;

public class DeleteMedicineController implements Controller {
    private final DeleteMedicine deleteMedicine;
    private final Decrypter decrypter;

    public DeleteMedicineController(DeleteMedicine deleteMedicine, Decrypter decrypter) {
        this.deleteMedicine = deleteMedicine;
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

        String medicineId = ctx.pathParam("id");
        deleteMedicine.execute(new DeleteMedicine.Params(medicineId, userId));
        HttpHelper.noContent(ctx);
    }
}
