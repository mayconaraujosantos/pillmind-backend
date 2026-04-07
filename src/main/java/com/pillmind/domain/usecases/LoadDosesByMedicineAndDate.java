package com.pillmind.domain.usecases;

import java.time.LocalDate;
import java.util.List;

import com.pillmind.domain.models.MedicineDose;

public interface LoadDosesByMedicineAndDate extends UseCase<LoadDosesByMedicineAndDate.Params, List<MedicineDose>> {

    record Params(String medicineId, String userId, LocalDate date) {}
}
