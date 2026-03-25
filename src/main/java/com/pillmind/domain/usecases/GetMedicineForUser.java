package com.pillmind.domain.usecases;

import com.pillmind.domain.models.Medicine;

public interface GetMedicineForUser extends UseCase<GetMedicineForUser.Params, Medicine> {

    record Params(String userId, String medicineId) {
    }
}
