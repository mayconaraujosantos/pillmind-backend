package com.pillmind.domain.models;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entidade Reminder — configura lembretes de horários para um medicamento.
 */
public record Reminder(
        String id,
        String userId,
        String medicineId,
        List<String> times,
        List<String> daysOfWeek,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) implements Entity {

    public Reminder withUpdated(List<String> newTimes, List<String> newDaysOfWeek, Boolean newActive) {
        return new Reminder(
                id,
                userId,
                medicineId,
                newTimes != null ? newTimes : times,
                newDaysOfWeek != null ? newDaysOfWeek : daysOfWeek,
                newActive != null ? newActive : active,
                createdAt,
                LocalDateTime.now());
    }
}
