package com.pillmind.data.protocols.db;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.pillmind.domain.models.MedicineDose;

public interface MedicineDoseRepository {

    MedicineDose upsert(MedicineDose dose);

    Optional<MedicineDose> findByMedicineAndDateAndTime(String medicineId, LocalDate date, String scheduledTime);

    List<MedicineDose> findByUserAndDate(String userId, LocalDate date);

    List<MedicineDose> findByMedicineAndDate(String medicineId, LocalDate date);
}
