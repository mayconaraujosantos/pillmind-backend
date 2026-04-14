package com.pillmind.domain.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Medicine")
class MedicineTest {

    private Medicine makeMedicine(String id, String userId) {
        return new Medicine(id, userId, "Aspirin", "500mg", "daily",
                List.of("08:00"), LocalDate.of(2024, 1, 1), null, "Take with water",
                null, "capsule", "Headache", 30, true,
                LocalDateTime.of(2024, 1, 1, 8, 0), LocalDateTime.of(2024, 1, 1, 8, 0));
    }

    @Test
    @DisplayName("Should create medicine with all fields")
    void shouldCreateMedicineWithAllFields() {
        var med = makeMedicine("med-1", "user-1");

        assertEquals("med-1", med.id());
        assertEquals("user-1", med.userId());
        assertEquals("Aspirin", med.name());
        assertEquals("500mg", med.dosage());
        assertEquals("daily", med.frequency());
        assertEquals(List.of("08:00"), med.times());
        assertEquals(LocalDate.of(2024, 1, 1), med.startDate());
        assertNull(med.endDate());
        assertEquals("Take with water", med.notes());
        assertNull(med.imageUrl());
        assertEquals("capsule", med.medicineType());
        assertEquals("Headache", med.prescribedFor());
        assertEquals(30, med.quantity());
        assertTrue(med.reminderOnEmpty());
        assertNotNull(med.createdAt());
        assertNotNull(med.updatedAt());
    }

    @Test
    @DisplayName("Should implement Entity interface")
    void shouldImplementEntityInterface() {
        var med = makeMedicine("med-id", "user-id");
        assertTrue(med instanceof Entity);
        assertEquals("med-id", med.id());
    }

    @Test
    @DisplayName("Should default null times to empty list")
    void shouldDefaultNullTimesToEmptyList() {
        var med = new Medicine("id", "userId", "Name", "dose", "freq",
                null, LocalDate.now(), null, null, null, "capsule",
                null, 1, false, LocalDateTime.now(), LocalDateTime.now());

        assertNotNull(med.times());
        assertTrue(med.times().isEmpty());
    }

    @Test
    @DisplayName("Should make times list immutable")
    void shouldReturnImmutableTimes() {
        var mutableTimes = new java.util.ArrayList<>(List.of("08:00", "20:00"));
        var med = new Medicine("id", "userId", "Name", "dose", "freq",
                mutableTimes, LocalDate.now(), null, null, null, "capsule",
                null, 1, false, LocalDateTime.now(), LocalDateTime.now());

        mutableTimes.add("12:00");

        assertEquals(2, med.times().size());
    }

    @Test
    @DisplayName("Should support optional fields as null")
    void shouldSupportNullOptionalFields() {
        var med = new Medicine("id", "userId", "Name", "dose", "freq",
                List.of(), LocalDate.now(), null, null, null, "capsule",
                null, 0, false, LocalDateTime.now(), LocalDateTime.now());

        assertNull(med.endDate());
        assertNull(med.notes());
        assertNull(med.imageUrl());
        assertNull(med.prescribedFor());
        assertFalse(med.reminderOnEmpty());
    }
}
