package com.pillmind.data.protocols.db;

import java.util.List;
import java.util.Optional;

import com.pillmind.domain.models.Medicine;

public interface MedicineRepository {

    Medicine add(Medicine medicine);

    Medicine update(Medicine medicine);

    Optional<Medicine> findById(String id);

    List<Medicine> findByUserId(String userId);

    boolean delete(String id);
}
