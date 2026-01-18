package com.pillmind.domain.models;

/**
 * Entidade Account - representa uma conta de usuário
 */
public record Account(String id, String name, String email, String password, boolean googleAccount) implements Entity {
}
