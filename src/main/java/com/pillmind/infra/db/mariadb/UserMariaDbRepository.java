package com.pillmind.infra.db.mariadb;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pillmind.data.protocols.db.UserRepository;
import com.pillmind.domain.errors.DatabaseException;
import com.pillmind.domain.errors.NotFoundException;
import com.pillmind.domain.models.Gender;
import com.pillmind.domain.models.User;

@SuppressWarnings("java:S2139")
public class UserMariaDbRepository extends MariaDbRepository implements UserRepository {
    private static final Logger logger = LoggerFactory.getLogger(UserMariaDbRepository.class);

    public UserMariaDbRepository(Connection connection) {
        super(connection);
    }

    @Override
    public User add(User user) {
        String sql = "INSERT INTO users (id, name, email, date_of_birth, gender, picture_url, email_verified, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.id());
            stmt.setString(2, user.name());
            stmt.setString(3, user.email());
            stmt.setDate(4, user.dateOfBirth() != null ? Date.valueOf(user.dateOfBirth()) : null);
            stmt.setString(5, user.gender() != null ? user.gender().name() : null);
            stmt.setString(6, user.pictureUrl());
            stmt.setBoolean(7, user.emailVerified());
            stmt.setObject(8, user.createdAt());
            stmt.setObject(9, user.updatedAt());

            stmt.executeUpdate();
            logger.debug("✓ User created with id: {}", user.id());
            return user;
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao criar usuário", e);
        }
    }

    @Override
    public User update(User user) {
        String sql = "UPDATE users SET name = ?, email = ?, date_of_birth = ?, gender = ?, picture_url = ?, email_verified = ?, updated_at = ? WHERE id = ?";

        int rowsAffected;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.name());
            stmt.setString(2, user.email());
            stmt.setDate(3, user.dateOfBirth() != null ? Date.valueOf(user.dateOfBirth()) : null);
            stmt.setString(4, user.gender() != null ? user.gender().name() : null);
            stmt.setString(5, user.pictureUrl());
            stmt.setBoolean(6, user.emailVerified());
            stmt.setObject(7, user.updatedAt());
            stmt.setString(8, user.id());

            rowsAffected = stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao atualizar usuário", e);
        }

        if (rowsAffected == 0) {
            throw new NotFoundException("Usuário não encontrado: " + user.id());
        }

        logger.debug("✓ User updated with id: {}", user.id());
        return user;
    }

    @Override
    public Optional<User> findById(String id) {
        String sql = "SELECT id, name, email, date_of_birth, gender, picture_url, email_verified, created_at, updated_at " +
                     "FROM users WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao buscar usuário por id: " + id, e);
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT id, name, email, date_of_birth, gender, picture_url, email_verified, created_at, updated_at " +
                     "FROM users WHERE email = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao buscar usuário por email", e);
        }
    }

    @Override
    public boolean emailExists(String email) {
        String sql = "SELECT 1 FROM users WHERE email = ? LIMIT 1";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao verificar existência de email", e);
        }
    }

    @Override
    public boolean delete(String id) {
        String sql = "DELETE FROM users WHERE id = ?";

        int rowsAffected;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            rowsAffected = stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao deletar usuário", e);
        }

        boolean deleted = rowsAffected > 0;
        if (deleted) {
            logger.debug("✓ User deleted with id: {}", id);
        }
        return deleted;
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        Date dateOfBirth = rs.getDate("date_of_birth");
        LocalDate localDateOfBirth = dateOfBirth != null ? dateOfBirth.toLocalDate() : null;

        String genderStr = rs.getString("gender");
        Gender gender = genderStr != null ? Gender.fromString(genderStr) : null;

        return new User(
            rs.getString("id"),
            rs.getString("name"),
            rs.getString("email"),
            localDateOfBirth,
            gender,
            rs.getString("picture_url"),
            rs.getBoolean("email_verified"),
            rs.getTimestamp("created_at").toLocalDateTime(),
            rs.getTimestamp("updated_at").toLocalDateTime()
        );
    }
}
