package com.pillmind.domain.usecases;

import java.util.List;

import com.pillmind.domain.models.Reminder;

public interface LoadRemindersByUser extends UseCase<LoadRemindersByUser.Params, List<Reminder>> {

    record Params(String userId) {}
}
