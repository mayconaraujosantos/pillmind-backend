package com.pillmind.data.usecases;

import java.util.UUID;

import com.pillmind.data.protocols.db.MedicineRepository;
import com.pillmind.domain.models.Medicine;
import com.pillmind.domain.usecases.CreateMedicine;

public class DbCreateMedicine extends DbUseCase implements CreateMedicine {
    private final MedicineRepository medicineRepository;

    public DbCreateMedicine(MedicineRepository medicineRepository) {
        this.medicineRepository = medicineRepository;
    }

    @Override
    public Medicine execute(Params params) {
        var medicine = Medicine.builder()
                .id(UUID.randomUUID().toString())
                .userId(params.userId())
                .name(params.name())
                .dosage(params.dosage())
                .frequency(params.frequency())
                .times(params.times())
                .startDate(params.startDate())
                .endDate(params.endDate())
                .notes(params.notes())
                .imageUrl(params.imageUrl())
                .medicineType(params.medicineType())
                .prescribedFor(params.prescribedFor())
                .quantity(params.quantity())
                .reminderOnEmpty(params.reminderOnEmpty())
                .build();

        return medicineRepository.add(medicine);
    }
}
