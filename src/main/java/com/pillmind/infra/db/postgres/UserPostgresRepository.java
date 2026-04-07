package com.pillmind.infra.db.postgres;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Optional;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.JdbiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pillmind.data.protocols.db.UserRepository;
import com.pillmind.domain.errors.DatabaseException;
import com.pillmind.domain.errors.NotFoundException;
import com.pillmind.domain.models.Gender;
import com.pillmind.domain.models.User;

public class UserPostgresRepository extends PostgresRepository implements UserRepository {
    private static final Logger logger = LoggerFactory.getLogger(UserPostgresRepository.class);

    public UserPostgresRepository(Jdbi jdbi) {
        super(jdbi);
    }

    @Override
    public User add(User user) {
        try {
            jdbi.useHandle(h -> h.createUpdate(
                    "INSERT INTO users (id, name, email, date_of_birth, gender, picture_url, email_verified, created_at, updated_at) " +
                    "VALUES (:id, :name, :email, :dateOfBirth, :gender, :pictureUrl, :emailVerified, :createdAt, :updatedAt)")
                .bind("id", user.id())
                .bind("name", user.name())
                .bind("email", user.email())
                .bind("dateOfBirth", user.dateOfBirth() != null ? Date.valueOf(user.dateOfBirth()) : null)
                .bind("gender", user.gender() != null ? user.gender().name() : null)
                .bind("pictureUrl", user.pictureUrl())
                .bind("emailVerified", user.emailVerified())
                .bind("createdAt", Timestamp.valueOf(user.createdAt()))
                .bind("updatedAt", Timestamp.valueOf(user.updatedAt()))
                .execute());
            logger.debug("✓ User created with id: {}", user.id());
            return user;
        } catch (JdbiException e) {
            throw new DatabaseException("Erro ao criar usuário", e);
        }
    }

    @Override
    public User update(User user) {
        try {
            int rowsAffected = jdbi.withHandle(h -> h.createUpdate(
                    "UPDATE users SET name = :name, email = :email, date_of_birth = :dateOfBirth, " +
                    "gender = :gender, picture_url = :pictureUrl, email_verified = :emailVerified, updated_at = :updatedAt " +
                    "WHERE id = :id")
                .bind("name", user.name())
                .bind("email", user.email())
                .bind("dateOfBirth", user.dateOfBirth() != null ? Date.valueOf(user.dateOfBirth()) : null)
                .bind("gender", user.gender() != null ? user.gender().name() : null)
                .bind("pictureUrl", user.pictureUrl())
                .bind("emailVerified", user.emailVerified())
                .bind("updatedAt", Timestamp.valueOf(user.updatedAt()))
                .bind("id", user.id())
                .execute());

            if (rowsAffected == 0) {
                throw new NotFoundException("Usuário não encontrado: " + user.id());
            }
            logger.debug("✓ User updated with id: {}", user.id());
            return user;
        } catch (JdbiException e) {
            throw new DatabaseException("Erro ao atualizar usuário", e);
        }
    }

    @Override
    public Optional<User> findById(String id) {
        try {
            return jdbi.withHandle(h -> h.createQuery(
                    "SELECT id, name, email, date_of_birth, gender, picture_url, email_verified, created_at, updated_at " +
                    "FROM users WHERE id = :id")
                .bind("id", id)
                .map((rs, ctx) -> mapRow(rs))
                .findFirst());
        } catch (JdbiException e) {
            throw new DatabaseException("Erro ao buscar usuário por id: " + id, e);
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        try {
            return jdbi.withHandle(h -> h.createQuery(
                    "SELECT id, name, email, date_of_birth, gender, picture_url, email_verified, created_at, updated_at " +
                    "FROM users WHERE email = :email")
                .bind("email", email)
                .map((rs, ctx) -> mapRow(rs))
                .findFirst());
        } catch (JdbiException e) {
            throw new DatabaseException("Erro ao buscar usuário por email", e);
        }
    }

    @Override
    public boolean emailExists(String email) {
        try {
            return jdbi.withHandle(h -> h.createQuery(
                    "SELECT 1 FROM users WHERE email = :email LIMIT 1")
                .bind("email", email)
                .mapTo(Integer.class)
                .findFirst()
                .isPresent());
        } catch (JdbiException e) {
            throw new DatabaseException("Erro ao verificar existência de email", e);
        }
    }

    @Override
    public boolean delete(String id) {
        try {
            int rowsAffected = jdbi.withHandle(h -> h.createUpdate(
                    "DELETE FROM users WHERE id = :id")
                .bind("id", id)
                .execute());
            if (rowsAffected > 0) {
                logger.debug("✓ User deleted with id: {}", id);
            }
            return rowsAffected > 0;
        } catch (JdbiException e) {
            throw new DatabaseException("Erro ao deletar usuário", e);
        }
    }

    private User mapRow(java.sql.ResultSet rs) throws java.sql.SQLException {
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
