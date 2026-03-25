package com.pillmind.main.routes;

import com.pillmind.data.protocols.cryptography.Decrypter;
import com.pillmind.domain.usecases.CreateMedicineForUser;
import com.pillmind.domain.usecases.DeleteMedicineForUser;
import com.pillmind.domain.usecases.GetMedicineForUser;
import com.pillmind.domain.usecases.ListMedicinesForUser;
import com.pillmind.domain.usecases.UpdateMedicineForUser;
import com.pillmind.presentation.controllers.MedicineHttpController;
import com.pillmind.presentation.controllers.UploadMedicineImageController;

import io.javalin.Javalin;

/**
 * Rotas /api/medicines (CRUD autenticado).
 */
public class MedicineRoutes implements Routes {

    private final Decrypter decrypter;
    private final ListMedicinesForUser listMedicinesForUser;
    private final GetMedicineForUser getMedicineForUser;
    private final CreateMedicineForUser createMedicineForUser;
    private final UpdateMedicineForUser updateMedicineForUser;
    private final DeleteMedicineForUser deleteMedicineForUser;
    private final UploadMedicineImageController uploadMedicineImageController;

    public MedicineRoutes(
            Decrypter decrypter,
            ListMedicinesForUser listMedicinesForUser,
            GetMedicineForUser getMedicineForUser,
            CreateMedicineForUser createMedicineForUser,
            UpdateMedicineForUser updateMedicineForUser,
            DeleteMedicineForUser deleteMedicineForUser,
            UploadMedicineImageController uploadMedicineImageController) {
        this.decrypter = decrypter;
        this.listMedicinesForUser = listMedicinesForUser;
        this.getMedicineForUser = getMedicineForUser;
        this.createMedicineForUser = createMedicineForUser;
        this.updateMedicineForUser = updateMedicineForUser;
        this.deleteMedicineForUser = deleteMedicineForUser;
        this.uploadMedicineImageController = uploadMedicineImageController;
    }

    @Override
    public void setup(Javalin app) {
        var controller = new MedicineHttpController(
                decrypter,
                listMedicinesForUser,
                getMedicineForUser,
                createMedicineForUser,
                updateMedicineForUser,
                deleteMedicineForUser);

        app.get("/api/medicines", controller::list);
        app.post("/api/medicines", controller::create);
        app.post("/api/medicines/picture", uploadMedicineImageController::handle);
        app.get("/api/medicines/{id}", controller::getOne);
        app.put("/api/medicines/{id}", controller::update);
        app.delete("/api/medicines/{id}", controller::delete);
    }
}
