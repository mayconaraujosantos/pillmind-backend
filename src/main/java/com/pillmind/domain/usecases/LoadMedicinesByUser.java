package com.pillmind.domain.usecases;

import java.util.List;

import com.pillmind.domain.models.Medicine;

public interface LoadMedicinesByUser extends UseCase<LoadMedicinesByUser.Params, List<Medicine>> {

    record Params(String userId) {}
}
