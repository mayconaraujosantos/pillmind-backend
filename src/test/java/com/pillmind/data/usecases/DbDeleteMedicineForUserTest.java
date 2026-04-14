package com.pillmind.data.usecases;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.pillmind.data.protocols.db.MedicineRepository;
import com.pillmind.domain.errors.NotFoundException;
import com.pillmind.domain.usecases.DeleteMedicineForUser;

@DisplayName("DbDeleteMedicineForUser")
class DbDeleteMedicineForUserTest {

    private MedicineRepository medicineRepository;
    private DbDeleteMedicineForUser sut;

    @BeforeEach
    void setUp() {
        medicineRepository = mock(MedicineRepository.class);
        sut = new DbDeleteMedicineForUser(medicineRepository);
    }

    @Test
    @DisplayName("Should delete medicine and return null")
    void shouldDeleteMedicine() {
        when(medicineRepository.deleteByIdAndUserId("med-1", "user-id")).thenReturn(true);

        var result = sut.execute(new DeleteMedicineForUser.Params("user-id", "med-1"));

        assertNull(result);
        verify(medicineRepository).deleteByIdAndUserId("med-1", "user-id");
    }

    @Test
    @DisplayName("Should throw NotFoundException when medicine does not exist")
    void shouldThrowNotFoundWhenMedicineNotFound() {
        when(medicineRepository.deleteByIdAndUserId("nonexistent", "user-id")).thenReturn(false);

        assertThrows(NotFoundException.class,
                () -> sut.execute(new DeleteMedicineForUser.Params("user-id", "nonexistent")));
    }

    @Test
    @DisplayName("Should not delete medicine belonging to different user")
    void shouldNotDeleteMedicineOfDifferentUser() {
        when(medicineRepository.deleteByIdAndUserId("med-1", "other-user")).thenReturn(false);

        assertThrows(NotFoundException.class,
                () -> sut.execute(new DeleteMedicineForUser.Params("other-user", "med-1")));
    }
}
