package com.pillmind.presentation.protocols;

/**
 * Interface para validação de dados
 * @param <T> Tipo do objeto a ser validado
 */
@FunctionalInterface
public interface Validation<T> {
    void validate(T input);
}
