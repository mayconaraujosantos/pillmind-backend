package com.pillmind.data.usecases;

import com.pillmind.data.protocols.db.MedicineRepository;
import com.pillmind.domain.errors.NotFoundException;
import com.pillmind.domain.models.Medicine;
import com.pillmind.domain.usecases.LoadMedicineById;

public class DbLoadMedicineById extends DbUseCase implements LoadMedicineById {
    private final MedicineRepository medicineRepository;

    public DbLoadMedicineById(MedicineRepository medicineRepository) {
        this.medicineRepository = medicineRepository;
    }

    @Override
    public Medicine execute(Params params) {
        var medicine = medicineRepository.findById(params.id())
                .orElseThrow(() -> new NotFoundException("Medicamento não encontrado"));

        if (!medicine.userId().equals(params.userId())) {
            throw new NotFoundException("Medicamento não encontrado");
        }

        return medicine;
    }
}
