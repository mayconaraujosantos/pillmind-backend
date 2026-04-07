package com.pillmind.domain.usecases;

import java.util.List;

import com.pillmind.domain.models.Reminder;

public interface UpdateReminder extends UseCase<UpdateReminder.Params, Reminder> {

    record Params(
            String id,
            String userId,
            List<String> times,
            List<String> daysOfWeek,
            Boolean active) {}
}
