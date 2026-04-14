package com.pillmind.data.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.pillmind.data.protocols.db.MedicineRepository;
import com.pillmind.domain.errors.NotFoundException;
import com.pillmind.domain.models.Medicine;
import com.pillmind.domain.usecases.UpdateMedicineForUser;

@DisplayName("DbUpdateMedicineForUser")
class DbUpdateMedicineForUserTest {

    private MedicineRepository medicineRepository;
    private DbUpdateMedicineForUser sut;

    @BeforeEach
    void setUp() {
        medicineRepository = mock(MedicineRepository.class);
        sut = new DbUpdateMedicineForUser(medicineRepository);
    }

    private Medicine makeMedicine(String id, String userId, String name) {
        return new Medicine(id, userId, name, "500mg", "daily", List.of("08:00"),
                LocalDate.of(2024, 1, 1), null, null, null, "capsule", null, 30, true,
                LocalDateTime.of(2024, 1, 1, 8, 0), LocalDateTime.of(2024, 1, 1, 8, 0));
    }

    @Test
    @DisplayName("Should update and return medicine with updated fields")
    void shouldUpdateMedicineFields() {
        var existing = makeMedicine("med-1", "user-id", "Aspirin");
        when(medicineRepository.findByIdAndUserId("med-1", "user-id")).thenReturn(Optional.of(existing));

        var params = new UpdateMedicineForUser.Params(
                "user-id", "med-1", "Aspirin Plus", "1000mg", "twice daily",
                List.of("08:00", "20:00"), LocalDate.of(2024, 2, 1), null,
                "Take with food", null, "tablet", "Pain", 60, false);

        var result = sut.execute(params);

        assertNotNull(result);
        assertEquals("Aspirin Plus", result.name());
        assertEquals("1000mg", result.dosage());
        assertEquals("twice daily", result.frequency());
        verify(medicineRepository).update(any(Medicine.class));
    }

    @Test
    @DisplayName("Should throw NotFoundException when medicine not found")
    void shouldThrowNotFoundWhenMedicineNotFound() {
        when(medicineRepository.findByIdAndUserId("nonexistent", "user-id")).thenReturn(Optional.empty());

        var params = new UpdateMedicineForUser.Params(
                "user-id", "nonexistent", "Aspirin", "500mg", "daily",
                List.of(), LocalDate.now(), null, null, null, "capsule", null, 1, true);

        assertThrows(NotFoundException.class, () -> sut.execute(params));
        verify(medicineRepository, never()).update(any());
    }

    @Test
    @DisplayName("Should preserve original createdAt timestamp on update")
    void shouldPreserveCreatedAt() {
        var originalCreatedAt = LocalDateTime.of(2024, 1, 1, 8, 0);
        var existing = makeMedicine("med-1", "user-id", "Aspirin");
        when(medicineRepository.findByIdAndUserId("med-1", "user-id")).thenReturn(Optional.of(existing));

        var params = new UpdateMedicineForUser.Params(
                "user-id", "med-1", "New Name", "200mg", "daily",
                List.of(), LocalDate.now(), null, null, null, "capsule", null, 1, true);

        var result = sut.execute(params);

        assertEquals(originalCreatedAt, result.createdAt());
    }
}
