package com.pillmind.domain.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Entidade Medicine — representa um medicamento cadastrado pelo usuário
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
        Integer quantity,
        Boolean reminderOnEmpty,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) implements Entity {

    public Medicine withUpdated(
            String newName,
            String newDosage,
            String newFrequency,
            List<String> newTimes,
            LocalDate newStartDate,
            LocalDate newEndDate,
            String newNotes,
            String newImageUrl,
            String newMedicineType,
            String newPrescribedFor,
            Integer newQuantity,
            Boolean newReminderOnEmpty) {
        return new Builder()
                .id(id).userId(userId)
                .name(newName).dosage(newDosage).frequency(newFrequency).times(newTimes)
                .startDate(newStartDate).endDate(newEndDate).notes(newNotes).imageUrl(newImageUrl)
                .medicineType(newMedicineType).prescribedFor(newPrescribedFor)
                .quantity(newQuantity).reminderOnEmpty(newReminderOnEmpty)
                .createdAt(createdAt).updatedAt(LocalDateTime.now())
                .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String id;
        private String userId;
        private String name;
        private String dosage;
        private String frequency;
        private List<String> times;
        private LocalDate startDate;
        private LocalDate endDate;
        private String notes;
        private String imageUrl;
        private String medicineType;
        private String prescribedFor;
        private Integer quantity;
        private Boolean reminderOnEmpty;
        private LocalDateTime createdAt = LocalDateTime.now();
        private LocalDateTime updatedAt = LocalDateTime.now();

        private Builder() {}

        public Builder id(String id) { this.id = id; return this; }
        public Builder userId(String userId) { this.userId = userId; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder dosage(String dosage) { this.dosage = dosage; return this; }
        public Builder frequency(String frequency) { this.frequency = frequency; return this; }
        public Builder times(List<String> times) { this.times = times; return this; }
        public Builder startDate(LocalDate startDate) { this.startDate = startDate; return this; }
        public Builder endDate(LocalDate endDate) { this.endDate = endDate; return this; }
        public Builder notes(String notes) { this.notes = notes; return this; }
        public Builder imageUrl(String imageUrl) { this.imageUrl = imageUrl; return this; }
        public Builder medicineType(String medicineType) { this.medicineType = medicineType; return this; }
        public Builder prescribedFor(String prescribedFor) { this.prescribedFor = prescribedFor; return this; }
        public Builder quantity(Integer quantity) { this.quantity = quantity; return this; }
        public Builder reminderOnEmpty(Boolean reminderOnEmpty) { this.reminderOnEmpty = reminderOnEmpty; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public Medicine build() {
            return new Medicine(id, userId, name, dosage, frequency, times, startDate, endDate,
                    notes, imageUrl, medicineType, prescribedFor, quantity, reminderOnEmpty,
                    createdAt, updatedAt);
        }
    }
}
