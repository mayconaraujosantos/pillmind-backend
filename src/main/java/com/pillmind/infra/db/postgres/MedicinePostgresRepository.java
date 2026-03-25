package com.pillmind.infra.db.postgres;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pillmind.data.protocols.db.MedicineRepository;
import com.pillmind.domain.errors.NotFoundException;
import com.pillmind.domain.models.Medicine;

/**
 * JDBC para medicamentos (SQLite/Postgres).
 */
public class MedicinePostgresRepository extends PostgresRepository implements MedicineRepository {

    private static final Logger logger = LoggerFactory.getLogger(MedicinePostgresRepository.class);
    private static final ObjectMapper JSON = new ObjectMapper();

    public MedicinePostgresRepository(Connection connection) {
        super(connection);
    }

    @Override
    public List<Medicine> findAllByUserId(String userId) {
        String sql = """
                SELECT id, user_id, name, dosage, frequency, times_json, start_date, end_date, notes, image_url,
                       medicine_type, prescribed_for, quantity, reminder_on_empty, created_at, updated_at
                FROM medicines WHERE user_id = ? ORDER BY start_date ASC, name ASC
                """;
        List<Medicine> out = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    out.add(mapRow(rs));
                }
            }
            return out;
        } catch (SQLException e) {
            logger.error("findAllByUserId: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao listar medicamentos", e);
        }
    }

    @Override
    public Optional<Medicine> findByIdAndUserId(String id, String userId) {
        String sql = """
                SELECT id, user_id, name, dosage, frequency, times_json, start_date, end_date, notes, image_url,
                       medicine_type, prescribed_for, quantity, reminder_on_empty, created_at, updated_at
                FROM medicines WHERE id = ? AND user_id = ?
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.setString(2, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            logger.error("findByIdAndUserId: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao buscar medicamento", e);
        }
    }

    @Override
    public Medicine insert(Medicine medicine) {
        String sql = """
                INSERT INTO medicines (id, user_id, name, dosage, frequency, times_json, start_date, end_date, notes, image_url,
                    medicine_type, prescribed_for, quantity, reminder_on_empty, created_at, updated_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, medicine.id());
            stmt.setString(2, medicine.userId());
            stmt.setString(3, medicine.name());
            stmt.setString(4, medicine.dosage());
            stmt.setString(5, medicine.frequency());
            stmt.setString(6, serializeTimes(medicine.times()));
            stmt.setDate(7, Date.valueOf(medicine.startDate()));
            stmt.setDate(8, medicine.endDate() != null ? Date.valueOf(medicine.endDate()) : null);
            stmt.setString(9, medicine.notes());
            stmt.setString(10, medicine.imageUrl());
            stmt.setString(11, medicine.medicineType());
            stmt.setString(12, medicine.prescribedFor());
            stmt.setInt(13, medicine.quantity());
            stmt.setInt(14, medicine.reminderOnEmpty() ? 1 : 0);
            setTimestamp(stmt, 15, medicine.createdAt());
            setTimestamp(stmt, 16, medicine.updatedAt());
            stmt.executeUpdate();
            return medicine;
        } catch (SQLException e) {
            logger.error("insert medicine: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao criar medicamento", e);
        }
    }

    @Override
    public void update(Medicine medicine) {
        String sql = """
                UPDATE medicines SET name = ?, dosage = ?, frequency = ?, times_json = ?, start_date = ?, end_date = ?, notes = ?, image_url = ?,
                    medicine_type = ?, prescribed_for = ?, quantity = ?, reminder_on_empty = ?, updated_at = ?
                WHERE id = ? AND user_id = ?
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, medicine.name());
            stmt.setString(2, medicine.dosage());
            stmt.setString(3, medicine.frequency());
            stmt.setString(4, serializeTimes(medicine.times()));
            stmt.setDate(5, Date.valueOf(medicine.startDate()));
            stmt.setDate(6, medicine.endDate() != null ? Date.valueOf(medicine.endDate()) : null);
            stmt.setString(7, medicine.notes());
            stmt.setString(8, medicine.imageUrl());
            stmt.setString(9, medicine.medicineType());
            stmt.setString(10, medicine.prescribedFor());
            stmt.setInt(11, medicine.quantity());
            stmt.setInt(12, medicine.reminderOnEmpty() ? 1 : 0);
            setTimestamp(stmt, 13, medicine.updatedAt());
            stmt.setString(14, medicine.id());
            stmt.setString(15, medicine.userId());
            int n = stmt.executeUpdate();
            if (n == 0) {
                throw new NotFoundException("Medicamento não encontrado");
            }
        } catch (SQLException e) {
            logger.error("update medicine: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao atualizar medicamento", e);
        }
    }

    @Override
    public boolean deleteByIdAndUserId(String id, String userId) {
        String sql = "DELETE FROM medicines WHERE id = ? AND user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.setString(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("delete medicine: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao remover medicamento", e);
        }
    }

    private static Medicine mapRow(ResultSet rs) throws SQLException {
        LocalDate end = null;
        Date endD = rs.getDate("end_date");
        if (endD != null) {
            end = endD.toLocalDate();
        }
        LocalDateTime created = rs.getTimestamp("created_at").toLocalDateTime();
        LocalDateTime updated = rs.getTimestamp("updated_at").toLocalDateTime();
        String medType = rs.getString("medicine_type");
        if (medType == null || medType.isBlank()) {
            medType = "capsule";
        }
        return new Medicine(
                rs.getString("id"),
                rs.getString("user_id"),
                rs.getString("name"),
                rs.getString("dosage"),
                rs.getString("frequency"),
                deserializeTimes(rs.getString("times_json")),
                rs.getDate("start_date").toLocalDate(),
                end,
                rs.getString("notes"),
                rs.getString("image_url"),
                medType,
                rs.getString("prescribed_for"),
                rs.getInt("quantity"),
                rs.getInt("reminder_on_empty") != 0,
                created,
                updated);
    }

    private static String serializeTimes(List<String> times) {
        try {
            return JSON.writeValueAsString(times != null ? times : List.of());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static List<String> deserializeTimes(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        try {
            return JSON.readValue(json, new TypeReference<List<String>>() {
            });
        } catch (Exception e) {
            logger.warn("times_json inválido, usando lista vazia: {}", json);
            return List.of();
        }
    }
}
