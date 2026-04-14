package com.pillmind.data.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.pillmind.data.protocols.db.MedicineRepository;
import com.pillmind.domain.models.Medicine;
import com.pillmind.domain.usecases.CreateMedicineForUser;

@DisplayName("DbCreateMedicineForUser")
class DbCreateMedicineForUserTest {

    private MedicineRepository medicineRepository;
    private DbCreateMedicineForUser sut;

    @BeforeEach
    void setUp() {
        medicineRepository = mock(MedicineRepository.class);
        sut = new DbCreateMedicineForUser(medicineRepository);
    }

    private Medicine makeMedicine(String id, String userId, String name) {
        return new Medicine(id, userId, name, "500mg", "daily", List.of("08:00"),
                LocalDate.now(), null, null, null, "capsule", null, 30, true,
                LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    @DisplayName("Should create and return medicine with generated id")
    void shouldCreateMedicineWithGeneratedId() {
        var saved = makeMedicine("generated-id", "user-id", "Aspirin");
        when(medicineRepository.insert(any(Medicine.class))).thenReturn(saved);

        var params = new CreateMedicineForUser.Params(
                "user-id", "Aspirin", "500mg", "daily", List.of("08:00"),
                LocalDate.now(), null, null, null, "capsule", null, 30, true);

        var result = sut.execute(params);

        assertNotNull(result);
        assertEquals("generated-id", result.id());
        assertEquals("user-id", result.userId());
        assertEquals("Aspirin", result.name());
        verify(medicineRepository).insert(any(Medicine.class));
    }

    @Test
    @DisplayName("Should pass all fields to repository on insert")
    void shouldPassAllFieldsToRepository() {
        var startDate = LocalDate.of(2024, 1, 15);
        var endDate = LocalDate.of(2024, 6, 15);
        var saved = makeMedicine("med-id", "user-id", "Ibuprofen");
        when(medicineRepository.insert(any(Medicine.class))).thenReturn(saved);

        var params = new CreateMedicineForUser.Params(
                "user-id", "Ibuprofen", "400mg", "twice daily",
                List.of("08:00", "20:00"), startDate, endDate,
                "Take with food", null, "tablet", "Headache", 60, false);

        sut.execute(params);

        verify(medicineRepository).insert(any(Medicine.class));
    }

    @Test
    @DisplayName("Should handle null optional fields gracefully")
    void shouldHandleNullOptionalFields() {
        var saved = makeMedicine("med-id", "user-id", "Vitamin C");
        when(medicineRepository.insert(any(Medicine.class))).thenReturn(saved);

        var params = new CreateMedicineForUser.Params(
                "user-id", "Vitamin C", "1000mg", "daily",
                null, LocalDate.now(), null, null, null, "tablet", null, 1, true);

        var result = sut.execute(params);

        assertNotNull(result);
        verify(medicineRepository).insert(any(Medicine.class));
    }
}
