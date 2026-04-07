package com.pillmind.infra.db.postgres;

import java.sql.Timestamp;
import java.util.Optional;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.JdbiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pillmind.data.protocols.db.LocalAccountRepository;
import com.pillmind.domain.errors.DatabaseException;
import com.pillmind.domain.errors.NotFoundException;
import com.pillmind.domain.models.LocalAccount;

public class LocalAccountPostgresRepository extends PostgresRepository implements LocalAccountRepository {
    private static final Logger logger = LoggerFactory.getLogger(LocalAccountPostgresRepository.class);

    public LocalAccountPostgresRepository(Jdbi jdbi) {
        super(jdbi);
    }

    @Override
    public LocalAccount add(LocalAccount localAccount) {
        try {
            jdbi.useHandle(h -> h.createUpdate(
                    "INSERT INTO local_accounts (id, user_id, email, password_hash, last_login_at, created_at, updated_at) " +
                    "VALUES (:id, :userId, :email, :passwordHash, :lastLoginAt, :createdAt, :updatedAt)")
                .bind("id", localAccount.id())
                .bind("userId", localAccount.userId())
                .bind("email", localAccount.email())
                .bind("passwordHash", localAccount.passwordHash())
                .bind("lastLoginAt", localAccount.lastLoginAt() != null ? Timestamp.valueOf(localAccount.lastLoginAt()) : null)
                .bind("createdAt", Timestamp.valueOf(localAccount.createdAt()))
                .bind("updatedAt", Timestamp.valueOf(localAccount.updatedAt()))
                .execute());
            logger.debug("✓ LocalAccount created with id: {}", localAccount.id());
            return localAccount;
        } catch (JdbiException e) {
            throw new DatabaseException("Erro ao criar conta local", e);
        }
    }

    @Override
    public LocalAccount update(LocalAccount localAccount) {
        try {
            int rowsAffected = jdbi.withHandle(h -> h.createUpdate(
                    "UPDATE local_accounts SET email = :email, password_hash = :passwordHash, " +
                    "last_login_at = :lastLoginAt, updated_at = :updatedAt WHERE id = :id")
                .bind("email", localAccount.email())
                .bind("passwordHash", localAccount.passwordHash())
                .bind("lastLoginAt", localAccount.lastLoginAt() != null ? Timestamp.valueOf(localAccount.lastLoginAt()) : null)
                .bind("updatedAt", Timestamp.valueOf(localAccount.updatedAt()))
                .bind("id", localAccount.id())
                .execute());

            if (rowsAffected == 0) {
                throw new NotFoundException("Conta local não encontrada: " + localAccount.id());
            }
            logger.debug("✓ LocalAccount updated with id: {}", localAccount.id());
            return localAccount;
        } catch (JdbiException e) {
            throw new DatabaseException("Erro ao atualizar conta local", e);
        }
    }

    @Override
    public Optional<LocalAccount> findById(String id) {
        try {
            return jdbi.withHandle(h -> h.createQuery(
                    "SELECT id, user_id, email, password_hash, last_login_at, created_at, updated_at " +
                    "FROM local_accounts WHERE id = :id")
                .bind("id", id)
                .map((rs, ctx) -> mapRow(rs))
                .findFirst());
        } catch (JdbiException e) {
            throw new DatabaseException("Erro ao buscar conta local por id: " + id, e);
        }
    }

    @Override
    public Optional<LocalAccount> findByEmail(String email) {
        try {
            return jdbi.withHandle(h -> h.createQuery(
                    "SELECT id, user_id, email, password_hash, last_login_at, created_at, updated_at " +
                    "FROM local_accounts WHERE email = :email")
                .bind("email", email)
                .map((rs, ctx) -> mapRow(rs))
                .findFirst());
        } catch (JdbiException e) {
            throw new DatabaseException("Erro ao buscar conta local por email", e);
        }
    }

    @Override
    public Optional<LocalAccount> findByUserId(String userId) {
        try {
            return jdbi.withHandle(h -> h.createQuery(
                    "SELECT id, user_id, email, password_hash, last_login_at, created_at, updated_at " +
                    "FROM local_accounts WHERE user_id = :userId")
                .bind("userId", userId)
                .map((rs, ctx) -> mapRow(rs))
                .findFirst());
        } catch (JdbiException e) {
            throw new DatabaseException("Erro ao buscar conta local por userId: " + userId, e);
        }
    }

    @Override
    public boolean emailExists(String email) {
        try {
            return jdbi.withHandle(h -> h.createQuery(
                    "SELECT 1 FROM local_accounts WHERE email = :email LIMIT 1")
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
                    "DELETE FROM local_accounts WHERE id = :id")
                .bind("id", id)
                .execute());
            if (rowsAffected > 0) {
                logger.debug("✓ LocalAccount deleted with id: {}", id);
            }
            return rowsAffected > 0;
        } catch (JdbiException e) {
            throw new DatabaseException("Erro ao deletar conta local", e);
        }
    }

    @Override
    public boolean deleteByUserId(String userId) {
        try {
            int rowsAffected = jdbi.withHandle(h -> h.createUpdate(
                    "DELETE FROM local_accounts WHERE user_id = :userId")
                .bind("userId", userId)
                .execute());
            if (rowsAffected > 0) {
                logger.debug("✓ LocalAccount(s) deleted for user_id: {}", userId);
            }
            return rowsAffected > 0;
        } catch (JdbiException e) {
            throw new DatabaseException("Erro ao deletar contas locais por userId: " + userId, e);
        }
    }

    private LocalAccount mapRow(java.sql.ResultSet rs) throws java.sql.SQLException {
        return new LocalAccount(
            rs.getString("id"),
            rs.getString("user_id"),
            rs.getString("email"),
            rs.getString("password_hash"),
            rs.getTimestamp("last_login_at") != null ? rs.getTimestamp("last_login_at").toLocalDateTime() : null,
            rs.getTimestamp("created_at").toLocalDateTime(),
            rs.getTimestamp("updated_at").toLocalDateTime()
        );
    }
}
