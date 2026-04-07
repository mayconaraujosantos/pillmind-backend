package com.pillmind.domain.usecases;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.pillmind.domain.models.MedicineDose;

public interface TakeMedicineDose extends UseCase<TakeMedicineDose.Params, MedicineDose> {

    record Params(
            String userId,
            String medicineId,
            LocalDate date,
            String scheduledTime,
            LocalDateTime takenAt) {}
}
