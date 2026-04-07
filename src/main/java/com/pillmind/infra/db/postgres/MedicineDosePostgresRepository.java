package com.pillmind.infra.db.postgres;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.JdbiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pillmind.data.protocols.db.MedicineDoseRepository;
import com.pillmind.domain.errors.DatabaseException;
import com.pillmind.domain.models.MedicineDose;

public class MedicineDosePostgresRepository extends PostgresRepository implements MedicineDoseRepository {
    private static final Logger logger = LoggerFactory.getLogger(MedicineDosePostgresRepository.class);

    public MedicineDosePostgresRepository(Jdbi jdbi) {
        super(jdbi);
    }

    @Override
    public MedicineDose upsert(MedicineDose dose) {
        try {
            return jdbi.withHandle(h -> h.createQuery(
                    "INSERT INTO medicine_doses (id, user_id, medicine_id, date, scheduled_time, taken_at, skipped, created_at, updated_at) " +
                    "VALUES (:id, :userId, :medicineId, :date, :scheduledTime, :takenAt, :skipped, :createdAt, :updatedAt) " +
                    "ON CONFLICT (medicine_id, date, scheduled_time) " +
                    "DO UPDATE SET taken_at = EXCLUDED.taken_at, skipped = EXCLUDED.skipped, updated_at = EXCLUDED.updated_at " +
                    "RETURNING id, created_at")
                .bind("id", dose.id())
                .bind("userId", dose.userId())
                .bind("medicineId", dose.medicineId())
                .bind("date", Date.valueOf(dose.date()))
                .bind("scheduledTime", dose.scheduledTime())
                .bind("takenAt", dose.takenAt() != null ? Timestamp.valueOf(dose.takenAt()) : null)
                .bind("skipped", dose.skipped())
                .bind("createdAt", Timestamp.valueOf(dose.createdAt()))
                .bind("updatedAt", Timestamp.valueOf(dose.updatedAt()))
                .map((rs, ctx) -> {
                    String returnedId = rs.getString("id");
                    LocalDateTime returnedCreatedAt = rs.getTimestamp("created_at").toLocalDateTime();
                    logger.debug("✓ MedicineDose upserted: id={}", returnedId);
                    return new MedicineDose(returnedId, dose.userId(), dose.medicineId(), dose.date(),
                            dose.scheduledTime(), dose.takenAt(), dose.skipped(), returnedCreatedAt, dose.updatedAt());
                })
                .findFirst()
                .orElse(dose));
        } catch (JdbiException e) {
            throw new DatabaseException("Erro ao registrar dose do medicamento", e);
        }
    }

    @Override
    public Optional<MedicineDose> findByMedicineAndDateAndTime(String medicineId, LocalDate date, String scheduledTime) {
        try {
            return jdbi.withHandle(h -> h.createQuery(
                    "SELECT * FROM medicine_doses WHERE medicine_id = :medicineId AND date = :date AND scheduled_time = :scheduledTime")
                .bind("medicineId", medicineId)
                .bind("date", Date.valueOf(date))
                .bind("scheduledTime", scheduledTime)
                .map((rs, ctx) -> mapRow(rs))
                .findFirst());
        } catch (JdbiException e) {
            throw new DatabaseException("Erro ao buscar dose do medicamento", e);
        }
    }

    @Override
    public List<MedicineDose> findByUserAndDate(String userId, LocalDate date) {
        try {
            return jdbi.withHandle(h -> h.createQuery(
                    "SELECT * FROM medicine_doses WHERE user_id = :userId AND date = :date ORDER BY scheduled_time")
                .bind("userId", userId)
                .bind("date", Date.valueOf(date))
                .map((rs, ctx) -> mapRow(rs))
                .list());
        } catch (JdbiException e) {
            throw new DatabaseException("Erro ao buscar doses do dia", e);
        }
    }

    @Override
    public List<MedicineDose> findByMedicineAndDate(String medicineId, LocalDate date) {
        try {
            return jdbi.withHandle(h -> h.createQuery(
                    "SELECT * FROM medicine_doses WHERE medicine_id = :medicineId AND date = :date ORDER BY scheduled_time")
                .bind("medicineId", medicineId)
                .bind("date", Date.valueOf(date))
                .map((rs, ctx) -> mapRow(rs))
                .list());
        } catch (JdbiException e) {
            throw new DatabaseException("Erro ao buscar doses do medicamento na data", e);
        }
    }

    private MedicineDose mapRow(java.sql.ResultSet rs) throws java.sql.SQLException {
        java.sql.Timestamp takenAtTs = rs.getTimestamp("taken_at");
        LocalDateTime takenAt = takenAtTs != null ? takenAtTs.toLocalDateTime() : null;

        return new MedicineDose(
                rs.getString("id"),
                rs.getString("user_id"),
                rs.getString("medicine_id"),
                rs.getDate("date").toLocalDate(),
                rs.getString("scheduled_time"),
                takenAt,
                rs.getBoolean("skipped"),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getTimestamp("updated_at").toLocalDateTime());
    }
}
