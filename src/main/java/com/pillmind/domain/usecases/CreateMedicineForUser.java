package com.pillmind.domain.usecases;

import java.time.LocalDate;
import java.util.List;

import com.pillmind.domain.models.Medicine;

public interface CreateMedicineForUser extends UseCase<CreateMedicineForUser.Params, Medicine> {

    record Params(
            String userId,
            String name,
            String dosage,
            String frequency,
            List<String> times,
            LocalDate startDate,
            LocalDate endDate,
            String notes,
            String imageUrl,
            String medicineType,
            String prescribedFor,
            int quantity,
            boolean reminderOnEmpty) {
    }
}
