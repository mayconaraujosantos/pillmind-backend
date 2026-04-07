package com.pillmind.domain.usecases;

import java.time.LocalDate;

import com.pillmind.domain.models.MedicineDose;

public interface SkipMedicineDose extends UseCase<SkipMedicineDose.Params, MedicineDose> {

    record Params(
            String userId,
            String medicineId,
            LocalDate date,
            String scheduledTime) {}
}
