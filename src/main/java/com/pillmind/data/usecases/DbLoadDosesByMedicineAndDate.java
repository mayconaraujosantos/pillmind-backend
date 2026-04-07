package com.pillmind.data.usecases;

import java.util.List;

import com.pillmind.data.protocols.db.MedicineDoseRepository;
import com.pillmind.domain.models.MedicineDose;
import com.pillmind.domain.usecases.LoadDosesByMedicineAndDate;

public class DbLoadDosesByMedicineAndDate extends DbUseCase implements LoadDosesByMedicineAndDate {
    private final MedicineDoseRepository medicineDoseRepository;

    public DbLoadDosesByMedicineAndDate(MedicineDoseRepository medicineDoseRepository) {
        this.medicineDoseRepository = medicineDoseRepository;
    }

    @Override
    public List<MedicineDose> execute(Params params) {
        return medicineDoseRepository.findByMedicineAndDate(params.medicineId(), params.date());
    }
}
