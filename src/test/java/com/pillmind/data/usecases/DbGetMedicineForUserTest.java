package com.pillmind.data.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
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
import com.pillmind.domain.usecases.GetMedicineForUser;

@DisplayName("DbGetMedicineForUser")
class DbGetMedicineForUserTest {

    private MedicineRepository medicineRepository;
    private DbGetMedicineForUser sut;

    @BeforeEach
    void setUp() {
        medicineRepository = mock(MedicineRepository.class);
        sut = new DbGetMedicineForUser(medicineRepository);
    }

    private Medicine makeMedicine(String id, String userId, String name) {
        return new Medicine(id, userId, name, "500mg", "daily", List.of("08:00"),
                LocalDate.now(), null, null, null, "capsule", null, 30, true,
                LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    @DisplayName("Should return medicine when found")
    void shouldReturnMedicineWhenFound() {
        var medicine = makeMedicine("med-1", "user-id", "Aspirin");
        when(medicineRepository.findByIdAndUserId("med-1", "user-id")).thenReturn(Optional.of(medicine));

        var result = sut.execute(new GetMedicineForUser.Params("user-id", "med-1"));

        assertNotNull(result);
        assertEquals("med-1", result.id());
        assertEquals("user-id", result.userId());
        verify(medicineRepository).findByIdAndUserId("med-1", "user-id");
    }

    @Test
    @DisplayName("Should throw NotFoundException when medicine not found")
    void shouldThrowNotFoundWhenMedicineNotFound() {
        when(medicineRepository.findByIdAndUserId("nonexistent", "user-id")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> sut.execute(new GetMedicineForUser.Params("user-id", "nonexistent")));
    }

    @Test
    @DisplayName("Should not return medicine belonging to different user")
    void shouldNotReturnMedicineForDifferentUser() {
        when(medicineRepository.findByIdAndUserId("med-1", "other-user")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> sut.execute(new GetMedicineForUser.Params("other-user", "med-1")));
    }
}
