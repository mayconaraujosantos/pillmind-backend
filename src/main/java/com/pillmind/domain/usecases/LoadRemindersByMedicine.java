package com.pillmind.domain.usecases;

import java.util.List;

import com.pillmind.domain.models.Reminder;

public interface LoadRemindersByMedicine extends UseCase<LoadRemindersByMedicine.Params, List<Reminder>> {

    record Params(String userId, String medicineId) {}
}
