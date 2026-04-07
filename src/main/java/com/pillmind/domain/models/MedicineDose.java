package com.pillmind.domain.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidade MedicineDose — registra uma dose tomada ou pulada de um medicamento
 */
public record MedicineDose(
        String id,
        String userId,
        String medicineId,
        LocalDate date,
        String scheduledTime,
        LocalDateTime takenAt,
        boolean skipped,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) implements Entity {

    public MedicineDose asTaken(LocalDateTime takenAtTime) {
        return new MedicineDose(id, userId, medicineId, date, scheduledTime,
                takenAtTime, false, createdAt, LocalDateTime.now());
    }

    public MedicineDose asSkipped() {
        return new MedicineDose(id, userId, medicineId, date, scheduledTime,
                null, true, createdAt, LocalDateTime.now());
    }
}
