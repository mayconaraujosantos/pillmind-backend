package com.pillmind.data.usecases;

import java.time.LocalDateTime;
import java.util.UUID;

import com.pillmind.data.protocols.db.MedicineDoseRepository;
import com.pillmind.domain.models.MedicineDose;
import com.pillmind.domain.usecases.SkipMedicineDose;

public class DbSkipMedicineDose extends DbUseCase implements SkipMedicineDose {
    private final MedicineDoseRepository medicineDoseRepository;

    public DbSkipMedicineDose(MedicineDoseRepository medicineDoseRepository) {
        this.medicineDoseRepository = medicineDoseRepository;
    }

    @Override
    public MedicineDose execute(Params params) {
        var dose = new MedicineDose(
                UUID.randomUUID().toString(),
                params.userId(),
                params.medicineId(),
                params.date(),
                params.scheduledTime(),
                null,
                true,
                LocalDateTime.now(),
                LocalDateTime.now());

        return medicineDoseRepository.upsert(dose);
    }
}
