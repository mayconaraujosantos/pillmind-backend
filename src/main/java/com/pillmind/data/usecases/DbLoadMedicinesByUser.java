package com.pillmind.data.usecases;

import java.util.List;

import com.pillmind.data.protocols.db.MedicineRepository;
import com.pillmind.domain.models.Medicine;
import com.pillmind.domain.usecases.LoadMedicinesByUser;

public class DbLoadMedicinesByUser extends DbUseCase implements LoadMedicinesByUser {
    private final MedicineRepository medicineRepository;

    public DbLoadMedicinesByUser(MedicineRepository medicineRepository) {
        this.medicineRepository = medicineRepository;
    }

    @Override
    public List<Medicine> execute(Params params) {
        return medicineRepository.findByUserId(params.userId());
    }
}
