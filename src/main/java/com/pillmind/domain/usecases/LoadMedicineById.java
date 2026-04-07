package com.pillmind.domain.usecases;

import com.pillmind.domain.models.Medicine;

public interface LoadMedicineById extends UseCase<LoadMedicineById.Params, Medicine> {

    record Params(String id, String userId) {}
}
