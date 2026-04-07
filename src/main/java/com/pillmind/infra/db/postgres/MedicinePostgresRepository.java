package com.pillmind.infra.db.postgres;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.JdbiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pillmind.data.protocols.db.MedicineRepository;
import com.pillmind.domain.errors.DatabaseException;
import com.pillmind.domain.errors.NotFoundException;
import com.pillmind.domain.models.Medicine;

public class MedicinePostgresRepository extends PostgresRepository implements MedicineRepository {
    private static final Logger logger = LoggerFactory.getLogger(MedicinePostgresRepository.class);

    public MedicinePostgresRepository(Jdbi jdbi) {
        super(jdbi);
    }

    @Override
    public Medicine add(Medicine medicine) {
        try {
            jdbi.useHandle(h -> h.createUpdate(
                    "INSERT INTO medicines (id, user_id, name, dosage, frequency, times, start_date, end_date, " +
                    "notes, image_url, medicine_type, prescribed_for, quantity, reminder_on_empty, created_at, updated_at) " +
                    "VALUES (:id, :userId, :name, :dosage, :frequency, :times, :startDate, :endDate, " +
                    ":notes, :imageUrl, :medicineType, :prescribedFor, :quantity, :reminderOnEmpty, :createdAt, :updatedAt)")
                .bind("id", medicine.id())
                .bind("userId", medicine.userId())
                .bind("name", medicine.name())
                .bind("dosage", medicine.dosage())
                .bind("frequency", medicine.frequency())
                .bind("times", serializeTimes(medicine.times()))
                .bind("startDate", Date.valueOf(medicine.startDate()))
                .bind("endDate", medicine.endDate() != null ? Date.valueOf(medicine.endDate()) : null)
                .bind("notes", medicine.notes())
                .bind("imageUrl", medicine.imageUrl())
                .bind("medicineType", medicine.medicineType())
                .bind("prescribedFor", medicine.prescribedFor())
                .bind("quantity", medicine.quantity())
                .bind("reminderOnEmpty", medicine.reminderOnEmpty())
                .bind("createdAt", Timestamp.valueOf(medicine.createdAt()))
                .bind("updatedAt", Timestamp.valueOf(medicine.updatedAt()))
                .execute());
            logger.debug("✓ Medicine created: id={}", medicine.id());
            return medicine;
        } catch (JdbiException e) {
            throw new DatabaseException("Erro ao criar medicamento", e);
        }
    }

    @Override
    public Medicine update(Medicine medicine) {
        try {
            int rowsAffected = jdbi.withHandle(h -> h.createUpdate(
                    "UPDATE medicines SET name = :name, dosage = :dosage, frequency = :frequency, times = :times, " +
                    "start_date = :startDate, end_date = :endDate, notes = :notes, image_url = :imageUrl, " +
                    "medicine_type = :medicineType, prescribed_for = :prescribedFor, quantity = :quantity, " +
                    "reminder_on_empty = :reminderOnEmpty, updated_at = :updatedAt WHERE id = :id AND user_id = :userId")
                .bind("name", medicine.name())
                .bind("dosage", medicine.dosage())
                .bind("frequency", medicine.frequency())
                .bind("times", serializeTimes(medicine.times()))
                .bind("startDate", Date.valueOf(medicine.startDate()))
                .bind("endDate", medicine.endDate() != null ? Date.valueOf(medicine.endDate()) : null)
                .bind("notes", medicine.notes())
                .bind("imageUrl", medicine.imageUrl())
                .bind("medicineType", medicine.medicineType())
                .bind("prescribedFor", medicine.prescribedFor())
                .bind("quantity", medicine.quantity())
                .bind("reminderOnEmpty", medicine.reminderOnEmpty())
                .bind("updatedAt", Timestamp.valueOf(medicine.updatedAt()))
                .bind("id", medicine.id())
                .bind("userId", medicine.userId())
                .execute());

            if (rowsAffected == 0) {
                throw new NotFoundException("Medicamento não encontrado: " + medicine.id());
            }
            logger.debug("✓ Medicine updated: id={}", medicine.id());
            return medicine;
        } catch (JdbiException e) {
            throw new DatabaseException("Erro ao atualizar medicamento", e);
        }
    }

    @Override
    public Optional<Medicine> findById(String id) {
        try {
            return jdbi.withHandle(h -> h.createQuery(
                    "SELECT id, user_id, name, dosage, frequency, times, start_date, end_date, " +
                    "notes, image_url, medicine_type, prescribed_for, quantity, reminder_on_empty, " +
                    "created_at, updated_at FROM medicines WHERE id = :id")
                .bind("id", id)
                .map((rs, ctx) -> mapRow(rs))
                .findFirst());
        } catch (JdbiException e) {
            throw new DatabaseException("Erro ao buscar medicamento por id: " + id, e);
        }
    }

    @Override
    public List<Medicine> findByUserId(String userId) {
        try {
            return jdbi.withHandle(h -> h.createQuery(
                    "SELECT id, user_id, name, dosage, frequency, times, start_date, end_date, " +
                    "notes, image_url, medicine_type, prescribed_for, quantity, reminder_on_empty, " +
                    "created_at, updated_at FROM medicines WHERE user_id = :userId ORDER BY created_at DESC")
                .bind("userId", userId)
                .map((rs, ctx) -> mapRow(rs))
                .list());
        } catch (JdbiException e) {
            throw new DatabaseException("Erro ao buscar medicamentos do usuário: " + userId, e);
        }
    }

    @Override
    public boolean delete(String id) {
        try {
            int rowsAffected = jdbi.withHandle(h -> h.createUpdate(
                    "DELETE FROM medicines WHERE id = :id")
                .bind("id", id)
                .execute());
            if (rowsAffected > 0) {
                logger.debug("✓ Medicine deleted: id={}", id);
            }
            return rowsAffected > 0;
        } catch (JdbiException e) {
            throw new DatabaseException("Erro ao deletar medicamento", e);
        }
    }

    private Medicine mapRow(java.sql.ResultSet rs) throws java.sql.SQLException {
        Date endDate = rs.getDate("end_date");
        Integer quantity = rs.getObject("quantity") != null ? rs.getInt("quantity") : null;
        Boolean reminderOnEmpty = rs.getObject("reminder_on_empty") != null ? rs.getBoolean("reminder_on_empty") : null;

        return Medicine.builder()
                .id(rs.getString("id"))
                .userId(rs.getString("user_id"))
                .name(rs.getString("name"))
                .dosage(rs.getString("dosage"))
                .frequency(rs.getString("frequency"))
                .times(deserializeTimes(rs.getString("times")))
                .startDate(rs.getDate("start_date").toLocalDate())
                .endDate(endDate != null ? endDate.toLocalDate() : null)
                .notes(rs.getString("notes"))
                .imageUrl(rs.getString("image_url"))
                .medicineType(rs.getString("medicine_type"))
                .prescribedFor(rs.getString("prescribed_for"))
                .quantity(quantity)
                .reminderOnEmpty(reminderOnEmpty)
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                .build();
    }

    private String serializeTimes(List<String> times) {
        if (times == null || times.isEmpty()) {
            return "[]";
        }
        return "[\"" + String.join("\",\"", times) + "\"]";
    }

    private List<String> deserializeTimes(String timesJson) {
        if (timesJson == null || timesJson.isBlank() || timesJson.equals("[]")) {
            return new ArrayList<>();
        }
        String stripped = timesJson.trim().replaceAll("(^\\[)|(\\]$)", "");
        return new ArrayList<>(Arrays.stream(stripped.split(","))
                .map(s -> s.trim().replaceAll("(^\")|(\"$)", ""))
                .filter(s -> !s.isEmpty())
                .toList());
    }
}
