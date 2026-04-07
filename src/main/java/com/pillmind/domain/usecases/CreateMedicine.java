package com.pillmind.domain.usecases;

import java.time.LocalDate;
import java.util.List;

import com.pillmind.domain.models.Medicine;

public interface CreateMedicine extends UseCase<CreateMedicine.Params, Medicine> {

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
            Integer quantity,
            Boolean reminderOnEmpty) {}
}
