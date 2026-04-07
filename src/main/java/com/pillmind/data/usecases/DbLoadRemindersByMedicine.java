package com.pillmind.data.usecases;

import java.util.List;

import com.pillmind.data.protocols.db.MedicineRepository;
import com.pillmind.data.protocols.db.ReminderRepository;
import com.pillmind.domain.errors.NotFoundException;
import com.pillmind.domain.models.Reminder;
import com.pillmind.domain.usecases.LoadRemindersByMedicine;

public class DbLoadRemindersByMedicine extends DbUseCase implements LoadRemindersByMedicine {
    private final ReminderRepository reminderRepository;
    private final MedicineRepository medicineRepository;

    public DbLoadRemindersByMedicine(ReminderRepository reminderRepository, MedicineRepository medicineRepository) {
        this.reminderRepository = reminderRepository;
        this.medicineRepository = medicineRepository;
    }

    @Override
    public List<Reminder> execute(Params params) {
        var medicine = medicineRepository.findById(params.medicineId())
                .orElseThrow(() -> new NotFoundException("Medicamento não encontrado"));

        if (!medicine.userId().equals(params.userId())) {
            throw new NotFoundException("Medicamento não encontrado");
        }

        return reminderRepository.findByUserAndMedicine(params.userId(), params.medicineId());
    }
}
