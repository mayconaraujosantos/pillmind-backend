package com.pillmind.domain.usecases;

import java.time.LocalDate;
import java.util.List;

import com.pillmind.domain.models.MedicineDose;

public interface LoadDosesByDate extends UseCase<LoadDosesByDate.Params, List<MedicineDose>> {

    record Params(String userId, LocalDate date) {}
}
