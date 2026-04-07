package com.pillmind.main.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pillmind.data.protocols.cryptography.Decrypter;
import com.pillmind.domain.usecases.CreateReminder;
import com.pillmind.domain.usecases.DeleteReminder;
import com.pillmind.domain.usecases.LoadRemindersByMedicine;
import com.pillmind.domain.usecases.LoadRemindersByUser;
import com.pillmind.domain.usecases.UpdateReminder;
import com.pillmind.presentation.controllers.CreateReminderController;
import com.pillmind.presentation.controllers.DeleteReminderController;
import com.pillmind.presentation.controllers.ListRemindersByMedicineController;
import com.pillmind.presentation.controllers.ListRemindersController;
import com.pillmind.presentation.controllers.UpdateReminderController;

import io.javalin.Javalin;

public class ReminderRoutes implements Routes {
    private final CreateReminder createReminder;
    private final LoadRemindersByUser loadRemindersByUser;
    private final LoadRemindersByMedicine loadRemindersByMedicine;
    private final UpdateReminder updateReminder;
    private final DeleteReminder deleteReminder;
    private final Decrypter decrypter;
    private final ObjectMapper objectMapper;

    public ReminderRoutes(
            CreateReminder createReminder,
            LoadRemindersByUser loadRemindersByUser,
            LoadRemindersByMedicine loadRemindersByMedicine,
            UpdateReminder updateReminder,
            DeleteReminder deleteReminder,
            Decrypter decrypter,
            ObjectMapper objectMapper) {
        this.createReminder = createReminder;
        this.loadRemindersByUser = loadRemindersByUser;
        this.loadRemindersByMedicine = loadRemindersByMedicine;
        this.updateReminder = updateReminder;
        this.deleteReminder = deleteReminder;
        this.decrypter = decrypter;
        this.objectMapper = objectMapper;
    }

    @Override
    public void setup(Javalin app) {
        var createReminderController = new CreateReminderController(createReminder, decrypter, objectMapper);
        var listRemindersController = new ListRemindersController(loadRemindersByUser, decrypter);
        var listRemindersByMedicineController = new ListRemindersByMedicineController(loadRemindersByMedicine,
                decrypter);
        var updateReminderController = new UpdateReminderController(updateReminder, decrypter, objectMapper);
        var deleteReminderController = new DeleteReminderController(deleteReminder, decrypter);

        app.post("/api/reminders", createReminderController::handle);
        app.get("/api/reminders", listRemindersController::handle);
        app.get("/api/reminders/medicine/{medicineId}", listRemindersByMedicineController::handle);
        app.put("/api/reminders/{id}", updateReminderController::handle);
        app.delete("/api/reminders/{id}", deleteReminderController::handle);
    }
}
