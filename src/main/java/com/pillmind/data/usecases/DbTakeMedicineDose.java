package com.pillmind.data.usecases;

import java.time.LocalDateTime;
import java.util.UUID;

import com.pillmind.data.protocols.db.MedicineDoseRepository;
import com.pillmind.domain.models.MedicineDose;
import com.pillmind.domain.usecases.TakeMedicineDose;

public class DbTakeMedicineDose extends DbUseCase implements TakeMedicineDose {
    private final MedicineDoseRepository medicineDoseRepository;

    public DbTakeMedicineDose(MedicineDoseRepository medicineDoseRepository) {
        this.medicineDoseRepository = medicineDoseRepository;
    }

    @Override
    public MedicineDose execute(Params params) {
        LocalDateTime takenAt = params.takenAt() != null ? params.takenAt() : LocalDateTime.now();

        var dose = new MedicineDose(
                UUID.randomUUID().toString(),
                params.userId(),
                params.medicineId(),
                params.date(),
                params.scheduledTime(),
                takenAt,
                false,
                LocalDateTime.now(),
                LocalDateTime.now());

        return medicineDoseRepository.upsert(dose);
    }
}
