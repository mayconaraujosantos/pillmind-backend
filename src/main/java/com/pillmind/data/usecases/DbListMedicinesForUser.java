package com.pillmind.data.usecases;

import java.util.List;

import com.pillmind.data.protocols.db.MedicineRepository;
import com.pillmind.domain.models.Medicine;
import com.pillmind.domain.usecases.ListMedicinesForUser;

public class DbListMedicinesForUser extends DbUseCase implements ListMedicinesForUser {

    private final MedicineRepository medicineRepository;

    public DbListMedicinesForUser(MedicineRepository medicineRepository) {
        this.medicineRepository = medicineRepository;
    }

    @Override
    public List<Medicine> execute(Params params) {
        return medicineRepository.findAllByUserId(params.userId());
    }
}
