package com.pillmind.data.protocols.db;

import java.util.List;
import java.util.Optional;

import com.pillmind.domain.models.Reminder;

public interface ReminderRepository {

    Reminder add(Reminder reminder);

    Reminder update(Reminder reminder);

    Optional<Reminder> findById(String id);

    List<Reminder> findByUserId(String userId);

    List<Reminder> findByUserAndMedicine(String userId, String medicineId);

    boolean delete(String id);
}
