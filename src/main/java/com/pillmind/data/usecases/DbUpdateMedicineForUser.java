package com.pillmind.data.usecases;

import java.time.LocalDateTime;

import com.pillmind.data.protocols.db.MedicineRepository;
import com.pillmind.domain.errors.NotFoundException;
import com.pillmind.domain.models.Medicine;
import com.pillmind.domain.usecases.UpdateMedicineForUser;

public class DbUpdateMedicineForUser extends DbUseCase implements UpdateMedicineForUser {

    private final MedicineRepository medicineRepository;

    public DbUpdateMedicineForUser(MedicineRepository medicineRepository) {
        this.medicineRepository = medicineRepository;
    }

    @Override
    public Medicine execute(Params params) {
        Medicine existing = medicineRepository
                .findByIdAndUserId(params.medicineId(), params.userId())
                .orElseThrow(() -> new NotFoundException("Medicamento não encontrado"));

        var updated = new Medicine(
                existing.id(),
                existing.userId(),
                params.name(),
                params.dosage(),
                params.frequency(),
                params.times(),
                params.startDate(),
                params.endDate(),
                params.notes(),
                params.imageUrl(),
                params.medicineType(),
                params.prescribedFor(),
                params.quantity(),
                params.reminderOnEmpty(),
                existing.createdAt(),
                LocalDateTime.now());
        medicineRepository.update(updated);
        return updated;
    }
}
