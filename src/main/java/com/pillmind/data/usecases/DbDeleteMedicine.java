package com.pillmind.data.usecases;

import com.pillmind.data.protocols.db.MedicineRepository;
import com.pillmind.domain.errors.NotFoundException;
import com.pillmind.domain.usecases.DeleteMedicine;

public class DbDeleteMedicine extends DbUseCase implements DeleteMedicine {
    private final MedicineRepository medicineRepository;

    public DbDeleteMedicine(MedicineRepository medicineRepository) {
        this.medicineRepository = medicineRepository;
    }

    @Override
    public Void execute(Params params) {
        var existing = medicineRepository.findById(params.id())
                .orElseThrow(() -> new NotFoundException("Medicamento não encontrado"));

        if (!existing.userId().equals(params.userId())) {
            throw new NotFoundException("Medicamento não encontrado");
        }

        medicineRepository.delete(params.id());
        return null;
    }
}
