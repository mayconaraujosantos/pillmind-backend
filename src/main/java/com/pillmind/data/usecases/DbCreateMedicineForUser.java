package com.pillmind.data.usecases;

import java.time.LocalDateTime;
import java.util.UUID;

import com.pillmind.data.protocols.db.MedicineRepository;
import com.pillmind.domain.models.Medicine;
import com.pillmind.domain.usecases.CreateMedicineForUser;

public class DbCreateMedicineForUser extends DbUseCase implements CreateMedicineForUser {

    private final MedicineRepository medicineRepository;

    public DbCreateMedicineForUser(MedicineRepository medicineRepository) {
        this.medicineRepository = medicineRepository;
    }

    @Override
    public Medicine execute(Params params) {
        var now = LocalDateTime.now();
        var id = UUID.randomUUID().toString();
        var medicine = new Medicine(
                id,
                params.userId(),
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
                now,
                now);
        return medicineRepository.insert(medicine);
    }
}
