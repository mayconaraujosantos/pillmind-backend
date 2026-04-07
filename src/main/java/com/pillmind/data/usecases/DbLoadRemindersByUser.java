package com.pillmind.data.usecases;

import java.util.List;

import com.pillmind.data.protocols.db.ReminderRepository;
import com.pillmind.domain.models.Reminder;
import com.pillmind.domain.usecases.LoadRemindersByUser;

public class DbLoadRemindersByUser extends DbUseCase implements LoadRemindersByUser {
    private final ReminderRepository reminderRepository;

    public DbLoadRemindersByUser(ReminderRepository reminderRepository) {
        this.reminderRepository = reminderRepository;
    }

    @Override
    public List<Reminder> execute(Params params) {
        return reminderRepository.findByUserId(params.userId());
    }
}
