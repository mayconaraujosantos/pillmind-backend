package com.pillmind.main.factories;

/**
 * Interface base para factories
 * @param <T> Tipo do objeto a ser criado
 */
@FunctionalInterface
public interface Factory<T> {
    T make();
}
