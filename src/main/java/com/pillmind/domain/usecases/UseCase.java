package com.pillmind.domain.usecases;

/**
 * Interface base para todos os casos de uso
 * @param <T> Tipo do parâmetro de entrada
 * @param <R> Tipo do resultado
 */
@FunctionalInterface
public interface UseCase<T, R> {
    R execute(T params);
}
