package com.pillmind.data.usecases;

import com.pillmind.data.protocols.db.ReminderRepository;
import com.pillmind.domain.errors.NotFoundException;
import com.pillmind.domain.usecases.DeleteReminder;

public class DbDeleteReminder extends DbUseCase implements DeleteReminder {
    private final ReminderRepository reminderRepository;

    public DbDeleteReminder(ReminderRepository reminderRepository) {
        this.reminderRepository = reminderRepository;
    }

    @Override
    public Void execute(Params params) {
        var existing = reminderRepository.findById(params.id())
                .orElseThrow(() -> new NotFoundException("Lembrete não encontrado"));

        if (!existing.userId().equals(params.userId())) {
            throw new NotFoundException("Lembrete não encontrado");
        }

        reminderRepository.delete(params.id());
        return null;
    }
}
