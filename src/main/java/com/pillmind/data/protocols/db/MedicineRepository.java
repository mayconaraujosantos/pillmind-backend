package com.pillmind.data.protocols.db;

import java.util.List;
import java.util.Optional;

import com.pillmind.domain.models.Medicine;

/**
 * Persistência de medicamentos por usuário.
 */
public interface MedicineRepository {

    List<Medicine> findAllByUserId(String userId);

    Optional<Medicine> findByIdAndUserId(String id, String userId);

    Medicine insert(Medicine medicine);

    void update(Medicine medicine);

    /**
     * @return true se uma linha foi removida
     */
    boolean deleteByIdAndUserId(String id, String userId);
}
