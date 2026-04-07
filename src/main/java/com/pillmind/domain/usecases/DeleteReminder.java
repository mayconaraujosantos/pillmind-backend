package com.pillmind.domain.usecases;

public interface DeleteReminder extends UseCase<DeleteReminder.Params, Void> {

    record Params(String id, String userId) {}
}
