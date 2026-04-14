package com.pillmind.data.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import com.pillmind.domain.usecases.ListMedicinesForUser;

@DisplayName("DbListMedicinesForUser")
class DbListMedicinesForUserTest {

    private MedicineRepository medicineRepository;
    private DbListMedicinesForUser sut;

    @BeforeEach
    void setUp() {
        medicineRepository = mock(MedicineRepository.class);
        sut = new DbListMedicinesForUser(medicineRepository);
    }

    private Medicine makeMedicine(String id, String userId, String name) {
        return new Medicine(id, userId, name, "500mg", "daily", List.of("08:00"),
                LocalDate.now(), null, null, null, "capsule", null, 30, true,
                LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    @DisplayName("Should return list of medicines for user")
    void shouldReturnMedicinesForUser() {
        var medicines = List.of(
                makeMedicine("med-1", "user-id", "Aspirin"),
                makeMedicine("med-2", "user-id", "Ibuprofen"));
        when(medicineRepository.findAllByUserId("user-id")).thenReturn(medicines);

        var result = sut.execute(new ListMedicinesForUser.Params("user-id"));

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(medicineRepository).findAllByUserId("user-id");
    }

    @Test
    @DisplayName("Should return empty list when user has no medicines")
    void shouldReturnEmptyListWhenNoMedicines() {
        when(medicineRepository.findAllByUserId("user-id")).thenReturn(List.of());

        var result = sut.execute(new ListMedicinesForUser.Params("user-id"));

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should delegate to repository with correct userId")
    void shouldDelegateToRepositoryWithUserId() {
        when(medicineRepository.findAllByUserId("another-user")).thenReturn(List.of());

        sut.execute(new ListMedicinesForUser.Params("another-user"));

        verify(medicineRepository).findAllByUserId("another-user");
    }
}
