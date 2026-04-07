package com.pillmind.data.usecases;

import java.util.List;

import com.pillmind.data.protocols.db.MedicineDoseRepository;
import com.pillmind.domain.models.MedicineDose;
import com.pillmind.domain.usecases.LoadDosesByDate;

public class DbLoadDosesByDate extends DbUseCase implements LoadDosesByDate {
    private final MedicineDoseRepository medicineDoseRepository;

    public DbLoadDosesByDate(MedicineDoseRepository medicineDoseRepository) {
        this.medicineDoseRepository = medicineDoseRepository;
    }

    @Override
    public List<MedicineDose> execute(Params params) {
        return medicineDoseRepository.findByUserAndDate(params.userId(), params.date());
    }
}
