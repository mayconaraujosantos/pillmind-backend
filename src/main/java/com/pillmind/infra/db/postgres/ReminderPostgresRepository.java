package com.pillmind.infra.db.postgres;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.JdbiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pillmind.data.protocols.db.ReminderRepository;
import com.pillmind.domain.errors.DatabaseException;
import com.pillmind.domain.errors.NotFoundException;
import com.pillmind.domain.models.Reminder;

public class ReminderPostgresRepository extends PostgresRepository implements ReminderRepository {
    private static final Logger logger = LoggerFactory.getLogger(ReminderPostgresRepository.class);

    public ReminderPostgresRepository(Jdbi jdbi) {
        super(jdbi);
    }

    @Override
    public Reminder add(Reminder reminder) {
        try {
            jdbi.useHandle(h -> h.createUpdate(
                    "INSERT INTO reminders (id, user_id, medicine_id, times, days_of_week, active, created_at, updated_at) " +
                    "VALUES (:id, :userId, :medicineId, :times, :daysOfWeek, :active, :createdAt, :updatedAt)")
                .bind("id", reminder.id())
                .bind("userId", reminder.userId())
                .bind("medicineId", reminder.medicineId())
                .bind("times", serializeList(reminder.times()))
                .bind("daysOfWeek", serializeList(reminder.daysOfWeek()))
                .bind("active", reminder.active())
                .bind("createdAt", Timestamp.valueOf(reminder.createdAt()))
                .bind("updatedAt", Timestamp.valueOf(reminder.updatedAt()))
                .execute());
            logger.debug("✓ Reminder created: id={}", reminder.id());
            return reminder;
        } catch (JdbiException e) {
            throw new DatabaseException("Erro ao criar lembrete", e);
        }
    }

    @Override
    public Reminder update(Reminder reminder) {
        try {
            int rowsAffected = jdbi.withHandle(h -> h.createUpdate(
                    "UPDATE reminders SET times = :times, days_of_week = :daysOfWeek, active = :active, " +
                    "updated_at = :updatedAt WHERE id = :id AND user_id = :userId")
                .bind("times", serializeList(reminder.times()))
                .bind("daysOfWeek", serializeList(reminder.daysOfWeek()))
                .bind("active", reminder.active())
                .bind("updatedAt", Timestamp.valueOf(reminder.updatedAt()))
                .bind("id", reminder.id())
                .bind("userId", reminder.userId())
                .execute());

            if (rowsAffected == 0) {
                throw new NotFoundException("Lembrete não encontrado: " + reminder.id());
            }
            logger.debug("✓ Reminder updated: id={}", reminder.id());
            return reminder;
        } catch (JdbiException e) {
            throw new DatabaseException("Erro ao atualizar lembrete", e);
        }
    }

    @Override
    public Optional<Reminder> findById(String id) {
        try {
            return jdbi.withHandle(h -> h.createQuery(
                    "SELECT id, user_id, medicine_id, times, days_of_week, active, created_at, updated_at " +
                    "FROM reminders WHERE id = :id")
                .bind("id", id)
                .map((rs, ctx) -> mapRow(rs))
                .findFirst());
        } catch (JdbiException e) {
            throw new DatabaseException("Erro ao buscar lembrete por id: " + id, e);
        }
    }

    @Override
    public List<Reminder> findByUserId(String userId) {
        try {
            return jdbi.withHandle(h -> h.createQuery(
                    "SELECT id, user_id, medicine_id, times, days_of_week, active, created_at, updated_at " +
                    "FROM reminders WHERE user_id = :userId ORDER BY created_at DESC")
                .bind("userId", userId)
                .map((rs, ctx) -> mapRow(rs))
                .list());
        } catch (JdbiException e) {
            throw new DatabaseException("Erro ao buscar lembretes do usuário", e);
        }
    }

    @Override
    public List<Reminder> findByUserAndMedicine(String userId, String medicineId) {
        try {
            return jdbi.withHandle(h -> h.createQuery(
                    "SELECT id, user_id, medicine_id, times, days_of_week, active, created_at, updated_at " +
                    "FROM reminders WHERE user_id = :userId AND medicine_id = :medicineId ORDER BY created_at DESC")
                .bind("userId", userId)
                .bind("medicineId", medicineId)
                .map((rs, ctx) -> mapRow(rs))
                .list());
        } catch (JdbiException e) {
            throw new DatabaseException("Erro ao buscar lembretes por medicamento", e);
        }
    }

    @Override
    public boolean delete(String id) {
        try {
            int rowsAffected = jdbi.withHandle(h -> h.createUpdate(
                    "DELETE FROM reminders WHERE id = :id")
                .bind("id", id)
                .execute());
            if (rowsAffected > 0) {
                logger.debug("✓ Reminder deleted: id={}", id);
            }
            return rowsAffected > 0;
        } catch (JdbiException e) {
            throw new DatabaseException("Erro ao deletar lembrete", e);
        }
    }

    private Reminder mapRow(java.sql.ResultSet rs) throws java.sql.SQLException {
        return new Reminder(
                rs.getString("id"),
                rs.getString("user_id"),
                rs.getString("medicine_id"),
                deserializeList(rs.getString("times")),
                deserializeList(rs.getString("days_of_week")),
                rs.getBoolean("active"),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getTimestamp("updated_at").toLocalDateTime());
    }

    private String serializeList(List<String> values) {
        if (values == null || values.isEmpty()) {
            return "[]";
        }
        return "[\"" + String.join("\",\"", values) + "\"]";
    }

    private List<String> deserializeList(String json) {
        if (json == null || json.isBlank() || "[]".equals(json)) {
            return new ArrayList<>();
        }
        String stripped = json.trim().replaceAll("(^\\[)|(\\]$)", "");
        return new ArrayList<>(Arrays.stream(stripped.split(","))
                .map(s -> s.trim().replaceAll("(^\")|(\"$)", ""))
                .filter(s -> !s.isEmpty())
                .toList());
    }
}
