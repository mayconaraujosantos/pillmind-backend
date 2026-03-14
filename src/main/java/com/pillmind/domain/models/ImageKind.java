package com.pillmind.domain.models;

import com.pillmind.domain.errors.ValidationException;

public enum ImageKind {
    PROFILE,
    MEDICATION;

    public static ImageKind fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new ValidationException("Tipo de imagem é obrigatório");
        }

        try {
            return ImageKind.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Tipo de imagem inválido. Valores aceitos: PROFILE, MEDICATION");
        }
    }

    public long maxSizeInBytes() {
        return this == PROFILE ? 3L * 1024 * 1024 : 5L * 1024 * 1024;
    }

    public String defaultVariant() {
        return this == PROFILE ? "profile" : "public";
    }
}
