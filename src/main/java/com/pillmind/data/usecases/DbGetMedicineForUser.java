package com.pillmind.data.usecases;

import com.pillmind.data.protocols.db.MedicineRepository;
import com.pillmind.domain.errors.NotFoundException;
import com.pillmind.domain.models.Medicine;
import com.pillmind.domain.usecases.GetMedicineForUser;

public class DbGetMedicineForUser extends DbUseCase implements GetMedicineForUser {

    private final MedicineRepository medicineRepository;

    public DbGetMedicineForUser(MedicineRepository medicineRepository) {
        this.medicineRepository = medicineRepository;
    }

    @Override
    public Medicine execute(Params params) {
        return medicineRepository
                .findByIdAndUserId(params.medicineId(), params.userId())
                .orElseThrow(() -> new NotFoundException("Medicamento não encontrado"));
    }
}
