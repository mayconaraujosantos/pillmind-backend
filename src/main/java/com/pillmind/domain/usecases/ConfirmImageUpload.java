package com.pillmind.domain.usecases;

import com.pillmind.domain.models.ImageKind;

public interface ConfirmImageUpload extends UseCase<ConfirmImageUpload.Params, ConfirmImageUpload.Result> {
    record Params(
            String userId,
            ImageKind kind,
            String imageId
    ) {
    }

    record Result(
            String imageId,
            String deliveryUrl
    ) {
    }
}
