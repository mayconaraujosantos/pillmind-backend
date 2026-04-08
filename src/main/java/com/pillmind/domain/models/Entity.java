package com.pillmind.domain.models;

import java.time.LocalDateTime;

/**
 * Interface base para todas as entidades do domínio
 * Define os campos comuns que toda entidade deve ter
 */
public interface Entity {
    /**
     * Identificador único da entidade
     */
    String id();

    /**
     * Data e hora de criação da entidade
     */
    LocalDateTime createdAt();

    /**
     * Data e hora da última atualização da entidade
     */
    LocalDateTime updatedAt();

    /**
     * Verifica se a entidade é válida
     */
    default boolean isValid() {
        return id() != null && !id().isBlank() && createdAt() != null;
    }

    /**
     * Verifica se a entidade foi criada recentemente (últimas 24 horas)
     */
    default boolean isRecent() {
        if (createdAt() == null) {
            return false;
        }
        return createdAt().isAfter(LocalDateTime.now().minusDays(1));
    }

    /**
     * Verifica se a entidade foi modificada
     */
    default boolean wasModified() {
        if (createdAt() == null || updatedAt() == null) {
            return false;
        }
        return !createdAt().equals(updatedAt());
    }
}
