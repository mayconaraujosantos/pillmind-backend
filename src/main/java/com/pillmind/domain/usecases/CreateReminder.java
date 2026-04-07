package com.pillmind.domain.usecases;

import java.util.List;

import com.pillmind.domain.models.Reminder;

public interface CreateReminder extends UseCase<CreateReminder.Params, Reminder> {

    record Params(
            String userId,
            String medicineId,
            List<String> times,
            List<String> daysOfWeek,
            Boolean active) {}
}
