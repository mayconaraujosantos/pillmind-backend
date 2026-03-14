package com.pillmind.data.protocols.storage;

import com.pillmind.domain.models.ImageKind;

public interface ImageStorageGateway {
    DirectUploadData createDirectUploadUrl(CreateDirectUploadParams params);

    StoredImageData getImageDetails(String imageId);

    String buildDeliveryUrl(String imageId, String variant);

    record CreateDirectUploadParams(
            String userId,
            ImageKind kind,
            String fileName,
            String contentType
    ) {
    }

    record DirectUploadData(
            String imageId,
            String uploadUrl
    ) {
    }

    record StoredImageData(
            String imageId,
            boolean uploaded,
            String metadataUserId,
            String metadataKind
    ) {
    }
}
