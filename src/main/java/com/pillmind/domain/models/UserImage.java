package com.pillmind.domain.models;

import java.time.LocalDateTime;

public record UserImage(
        String id,
        String userId,
        String imageId,
        ImageKind kind,
        String deliveryUrl,
        ImageUploadStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) implements Entity {

    public UserImage(String id, String userId, String imageId, ImageKind kind, String deliveryUrl, ImageUploadStatus status) {
        this(id, userId, imageId, kind, deliveryUrl, status, LocalDateTime.now(), LocalDateTime.now());
    }

    public UserImage withConfirmedDeliveryUrl(String newDeliveryUrl) {
        return new UserImage(id, userId, imageId, kind, newDeliveryUrl, ImageUploadStatus.CONFIRMED, createdAt, LocalDateTime.now());
    }
}
