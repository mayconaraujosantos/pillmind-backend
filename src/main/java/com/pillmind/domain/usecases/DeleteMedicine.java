package com.pillmind.domain.usecases;

public interface DeleteMedicine extends UseCase<DeleteMedicine.Params, Void> {

    record Params(String id, String userId) {}
}
