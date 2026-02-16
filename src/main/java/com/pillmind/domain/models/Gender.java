package com.pillmind.domain.models;

/**
 * Enum para representar o gênero do usuário
 */
public enum Gender {
    MALE("Male", "Masculino"),
    FEMALE("Female", "Feminino"),
    OTHER("Other", "Outro"),
    PREFER_NOT_TO_SAY("Prefer not to say", "Prefiro não informar");

    private final String englishLabel;
    private final String portugueseLabel;

    Gender(String englishLabel, String portugueseLabel) {
        this.englishLabel = englishLabel;
        this.portugueseLabel = portugueseLabel;
    }

    public String getEnglishLabel() {
        return englishLabel;
    }

    public String getPortugueseLabel() {
        return portugueseLabel;
    }

    public String getValue() {
        return name();
    }

    /**
     * Converte string para enum, case insensitive
     */
    public static Gender fromString(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        
        try {
            return Gender.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Fallback para valores em português ou inglês
            return switch (value.toLowerCase()) {
                case "masculino", "male", "m" -> MALE;
                case "feminino", "female", "f" -> FEMALE;
                case "outro", "other", "o" -> OTHER;
                case "prefiro não informar", "prefer not to say", "n" -> PREFER_NOT_TO_SAY;
                default -> null;
            };
        }
    }
}