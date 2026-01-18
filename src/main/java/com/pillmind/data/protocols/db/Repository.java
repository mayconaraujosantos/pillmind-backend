package com.pillmind.data.protocols.db;

import java.util.List;
import java.util.Optional;

/**
 * Interface base para repositórios
 * @param <T> Tipo da entidade
 * @param <ID> Tipo do identificador
 */
public interface Repository<T, ID> {
    T save(T entity);
    Optional<T> findById(ID id);
    List<T> findAll();
    void deleteById(ID id);
    boolean existsById(ID id);
}
