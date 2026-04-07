package com.pillmind.data.usecases;

import com.pillmind.data.protocols.db.MedicineRepository;
import com.pillmind.domain.errors.NotFoundException;
import com.pillmind.domain.models.Medicine;
import com.pillmind.domain.usecases.UpdateMedicine;

public class DbUpdateMedicine extends DbUseCase implements UpdateMedicine {
    private final MedicineRepository medicineRepository;

    public DbUpdateMedicine(MedicineRepository medicineRepository) {
        this.medicineRepository = medicineRepository;
    }

    @Override
    public Medicine execute(Params params) {
        var existing = medicineRepository.findById(params.id())
                .orElseThrow(() -> new NotFoundException("Medicamento não encontrado"));

        if (!existing.userId().equals(params.userId())) {
            throw new NotFoundException("Medicamento não encontrado");
        }

        var updated = existing.withUpdated(
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
                params.reminderOnEmpty());

        return medicineRepository.update(updated);
    }
}
