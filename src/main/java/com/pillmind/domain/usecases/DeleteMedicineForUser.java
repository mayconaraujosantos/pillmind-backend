package com.pillmind.domain.usecases;

public interface DeleteMedicineForUser extends UseCase<DeleteMedicineForUser.Params, Void> {

    record Params(String userId, String medicineId) {
    }
}
