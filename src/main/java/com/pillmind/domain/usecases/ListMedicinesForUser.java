package com.pillmind.domain.usecases;

import java.util.List;

import com.pillmind.domain.models.Medicine;

public interface ListMedicinesForUser extends UseCase<ListMedicinesForUser.Params, List<Medicine>> {

    record Params(String userId) {
    }
}
