package com.pillmind.data.usecases;

import com.pillmind.data.protocols.db.MedicineRepository;
import com.pillmind.domain.errors.NotFoundException;
import com.pillmind.domain.usecases.DeleteMedicineForUser;

public class DbDeleteMedicineForUser extends DbUseCase implements DeleteMedicineForUser {

    private final MedicineRepository medicineRepository;

    public DbDeleteMedicineForUser(MedicineRepository medicineRepository) {
        this.medicineRepository = medicineRepository;
    }

    @Override
    public Void execute(Params params) {
        boolean removed = medicineRepository.deleteByIdAndUserId(params.medicineId(), params.userId());
        if (!removed) {
            throw new NotFoundException("Medicamento não encontrado");
        }
        return null;
    }
}
