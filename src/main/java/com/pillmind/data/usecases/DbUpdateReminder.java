package com.pillmind.data.usecases;

import com.pillmind.data.protocols.db.ReminderRepository;
import com.pillmind.domain.errors.NotFoundException;
import com.pillmind.domain.models.Reminder;
import com.pillmind.domain.usecases.UpdateReminder;

public class DbUpdateReminder extends DbUseCase implements UpdateReminder {
    private final ReminderRepository reminderRepository;

    public DbUpdateReminder(ReminderRepository reminderRepository) {
        this.reminderRepository = reminderRepository;
    }

    @Override
    public Reminder execute(Params params) {
        var existing = reminderRepository.findById(params.id())
                .orElseThrow(() -> new NotFoundException("Lembrete não encontrado"));

        if (!existing.userId().equals(params.userId())) {
            throw new NotFoundException("Lembrete não encontrado");
        }

        var updated = existing.withUpdated(params.times(), params.daysOfWeek(), params.active());
        return reminderRepository.update(updated);
    }
}
