package com.pillmind.main.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pillmind.data.protocols.cryptography.Decrypter;
import com.pillmind.domain.usecases.CreateMedicine;
import com.pillmind.domain.usecases.DeleteMedicine;
import com.pillmind.domain.usecases.LoadDosesByDate;
import com.pillmind.domain.usecases.LoadDosesByMedicineAndDate;
import com.pillmind.domain.usecases.LoadMedicineById;
import com.pillmind.domain.usecases.LoadMedicinesByUser;
import com.pillmind.domain.usecases.SkipMedicineDose;
import com.pillmind.domain.usecases.TakeMedicineDose;
import com.pillmind.domain.usecases.UpdateMedicine;
import com.pillmind.presentation.controllers.CreateMedicineController;
import com.pillmind.presentation.controllers.DeleteMedicineController;
import com.pillmind.presentation.controllers.GetDosesController;
import com.pillmind.presentation.controllers.GetMedicineByIdController;
import com.pillmind.presentation.controllers.ListMedicinesController;
import com.pillmind.presentation.controllers.SkipMedicineDoseController;
import com.pillmind.presentation.controllers.TakeMedicineDoseController;
import com.pillmind.presentation.controllers.UpdateMedicineController;

import io.javalin.Javalin;

public class MedicineRoutes implements Routes {
    private static final Logger logger = LoggerFactory.getLogger(MedicineRoutes.class);

    private static final String MEDICINE_BY_ID = "/api/medicines/{id}";

    /**
     * Agrupa os casos de uso relacionados a medicamentos para reduzir parâmetros no construtor.
     */
    public record MedicineUseCases(
            CreateMedicine createMedicine,
            UpdateMedicine updateMedicine,
            DeleteMedicine deleteMedicine,
            LoadMedicinesByUser loadMedicinesByUser,
            LoadMedicineById loadMedicineById) {}

    /**
     * Agrupa os casos de uso relacionados a doses para reduzir parâmetros no construtor.
     */
    public record DoseUseCases(
            TakeMedicineDose takeMedicineDose,
            SkipMedicineDose skipMedicineDose,
            LoadDosesByDate loadDosesByDate,
            LoadDosesByMedicineAndDate loadDosesByMedicineAndDate) {}

    private final MedicineUseCases medicineUseCases;
    private final DoseUseCases doseUseCases;
    private final Decrypter decrypter;
    private final ObjectMapper objectMapper;

    public MedicineRoutes(
            MedicineUseCases medicineUseCases,
            DoseUseCases doseUseCases,
            Decrypter decrypter,
            ObjectMapper objectMapper) {
        this.medicineUseCases = medicineUseCases;
        this.doseUseCases = doseUseCases;
        this.decrypter = decrypter;
        this.objectMapper = objectMapper;
    }

    @Override
    public void setup(Javalin app) {
        var listMedicinesController = new ListMedicinesController(medicineUseCases.loadMedicinesByUser(), decrypter);
        var getMedicineByIdController = new GetMedicineByIdController(medicineUseCases.loadMedicineById(), decrypter);
        var createMedicineController = new CreateMedicineController(medicineUseCases.createMedicine(), decrypter, objectMapper);
        var updateMedicineController = new UpdateMedicineController(medicineUseCases.updateMedicine(), decrypter, objectMapper);
        var deleteMedicineController = new DeleteMedicineController(medicineUseCases.deleteMedicine(), decrypter);
        var takeDoseController = new TakeMedicineDoseController(doseUseCases.takeMedicineDose(), decrypter, objectMapper);
        var skipDoseController = new SkipMedicineDoseController(doseUseCases.skipMedicineDose(), decrypter, objectMapper);
        var getDosesController = new GetDosesController(doseUseCases.loadDosesByDate(), doseUseCases.loadDosesByMedicineAndDate(), decrypter);

        app.get("/api/medicines", ctx -> {
            logger.info("→ GET /api/medicines");
            listMedicinesController.handle(ctx);
        });

        app.post("/api/medicines", ctx -> {
            logger.info("→ POST /api/medicines");
            createMedicineController.handle(ctx);
        });

        app.get("/api/medicines/doses/today", ctx -> {
            logger.info("→ GET /api/medicines/doses/today");
            getDosesController.handleByDate(ctx);
        });

        app.get("/api/medicines/doses", ctx -> {
            logger.info("→ GET /api/medicines/doses?date={}", ctx.queryParam("date"));
            getDosesController.handleByDate(ctx);
        });

        app.get(MEDICINE_BY_ID, ctx -> {
            logger.info("→ GET /api/medicines/{}", ctx.pathParam("id"));
            getMedicineByIdController.handle(ctx);
        });

        app.put(MEDICINE_BY_ID, ctx -> {
            logger.info("→ PUT /api/medicines/{}", ctx.pathParam("id"));
            updateMedicineController.handle(ctx);
        });

        app.delete(MEDICINE_BY_ID, ctx -> {
            logger.info("→ DELETE /api/medicines/{}", ctx.pathParam("id"));
            deleteMedicineController.handle(ctx);
        });

        app.post("/api/medicines/{id}/doses/take", ctx -> {
            logger.info("→ POST /api/medicines/{}/doses/take", ctx.pathParam("id"));
            takeDoseController.handle(ctx);
        });

        app.post("/api/medicines/{id}/doses/skip", ctx -> {
            logger.info("→ POST /api/medicines/{}/doses/skip", ctx.pathParam("id"));
            skipDoseController.handle(ctx);
        });

        app.get("/api/medicines/{id}/doses", ctx -> {
            logger.info("→ GET /api/medicines/{}/doses?date={}", ctx.pathParam("id"), ctx.queryParam("date"));
            getDosesController.handleByMedicineAndDate(ctx);
        });
    }
}
