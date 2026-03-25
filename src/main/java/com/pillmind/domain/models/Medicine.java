package com.pillmind.domain.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Medicamento associado a um usuário (lembretes / agenda).
 */
public record Medicine(
        String id,
        String userId,
        String name,
        String dosage,
        String frequency,
        List<String> times,
        LocalDate startDate,
        LocalDate endDate,
        String notes,
        String imageUrl,
        String medicineType,
        String prescribedFor,
        int quantity,
        boolean reminderOnEmpty,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) implements Entity {

    public Medicine {
        times = times == null ? List.of() : List.copyOf(times);
    }
}
