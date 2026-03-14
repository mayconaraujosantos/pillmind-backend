package com.pillmind.domain.usecases;

import com.pillmind.domain.models.ImageKind;

public interface RequestImageUpload extends UseCase<RequestImageUpload.Params, RequestImageUpload.Result> {
    record Params(
            String userId,
            ImageKind kind,
            String fileName,
            String contentType,
            long size
    ) {
    }

    record Result(
            String imageId,
            String uploadUrl
    ) {
    }
}
