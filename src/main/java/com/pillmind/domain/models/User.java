package com.pillmind.domain.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidade User - representa os dados do perfil do usuário
 * Separado das credenciais de autenticação
 */
public record User(
        String id,
        String name,
        String email,
        LocalDate dateOfBirth,
        Gender gender,
        String pictureUrl,
        boolean emailVerified,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) implements Entity {

    /**
     * Construtor para criação de novo usuário (timestamps automáticos)
     */
    public User(String id, String name, String email) {
        this(id, name, email, null, null, null, false, LocalDateTime.now(), LocalDateTime.now());
    }

    /**
     * Construtor com dados completos (timestamps automáticos)
     */
    public User(String id, String name, String email, LocalDate dateOfBirth, Gender gender, String pictureUrl) {
        this(id, name, email, dateOfBirth, gender, pictureUrl, false, LocalDateTime.now(), LocalDateTime.now());
    }

    /**
     * Atualiza dados do perfil
     */
    public User withUpdatedProfile(String newName, String newEmail, LocalDate newDateOfBirth, 
                                  Gender newGender, String newPictureUrl) {
        return new User(id, newName, newEmail, newDateOfBirth, newGender, newPictureUrl, 
                       emailVerified, createdAt, LocalDateTime.now());
    }

    /**
     * Atualiza apenas nome e foto (para OAuth2)
     */
    public User withUpdatedProfile(String newName, String newPictureUrl) {
        return new User(id, newName, email, dateOfBirth, gender, newPictureUrl, 
                       emailVerified, createdAt, LocalDateTime.now());
    }

    /**
     * Marca email como verificado
     */
    public User withEmailVerified(boolean verified) {
        return new User(id, name, email, dateOfBirth, gender, pictureUrl, 
                       verified, createdAt, LocalDateTime.now());
    }

    /**
     * Atualiza apenas timestamp
     */
    public User withUpdatedTimestamp() {
        return new User(id, name, email, dateOfBirth, gender, pictureUrl, 
                       emailVerified, createdAt, LocalDateTime.now());
    }

    /**
     * Verifica se o perfil está completo (tem todos os dados básicos)
     */
    public boolean isProfileComplete() {
        return name != null && !name.isBlank() && 
               email != null && !email.isBlank() &&
               dateOfBirth != null &&
               gender != null;
    }
}