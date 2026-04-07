package com.pillmind.data.usecases;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.pillmind.data.protocols.db.MedicineRepository;
import com.pillmind.data.protocols.db.ReminderRepository;
import com.pillmind.domain.errors.NotFoundException;
import com.pillmind.domain.models.Reminder;
import com.pillmind.domain.usecases.CreateReminder;

public class DbCreateReminder extends DbUseCase implements CreateReminder {
    private final ReminderRepository reminderRepository;
    private final MedicineRepository medicineRepository;

    public DbCreateReminder(ReminderRepository reminderRepository, MedicineRepository medicineRepository) {
        this.reminderRepository = reminderRepository;
        this.medicineRepository = medicineRepository;
    }

    @Override
    public Reminder execute(Params params) {
        var medicine = medicineRepository.findById(params.medicineId())
                .orElseThrow(() -> new NotFoundException("Medicamento não encontrado"));

        if (!medicine.userId().equals(params.userId())) {
            throw new NotFoundException("Medicamento não encontrado");
        }

        var now = LocalDateTime.now();
        var reminder = new Reminder(
                UUID.randomUUID().toString(),
                params.userId(),
                params.medicineId(),
                params.times() != null ? params.times() : List.of(),
                params.daysOfWeek() != null ? params.daysOfWeek() : List.of(),
                params.active() == null || params.active(),
                now,
                now);

        return reminderRepository.add(reminder);
    }
}
