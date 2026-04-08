package com.pillmind.infra.db.mariadb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pillmind.data.protocols.db.LocalAccountRepository;
import com.pillmind.domain.errors.DatabaseException;
import com.pillmind.domain.errors.NotFoundException;
import com.pillmind.domain.models.LocalAccount;

@SuppressWarnings("java:S2139")
public class LocalAccountMariaDbRepository extends MariaDbRepository implements LocalAccountRepository {
    private static final Logger logger = LoggerFactory.getLogger(LocalAccountMariaDbRepository.class);

    public LocalAccountMariaDbRepository(Connection connection) {
        super(connection);
    }

    @Override
    public LocalAccount add(LocalAccount localAccount) {
        String sql = "INSERT INTO local_accounts (id, user_id, email, password_hash, last_login_at, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, localAccount.id());
            stmt.setString(2, localAccount.userId());
            stmt.setString(3, localAccount.email());
            stmt.setString(4, localAccount.passwordHash());
            stmt.setObject(5, localAccount.lastLoginAt());
            stmt.setObject(6, localAccount.createdAt());
            stmt.setObject(7, localAccount.updatedAt());

            stmt.executeUpdate();
            logger.debug("✓ LocalAccount created with id: {}", localAccount.id());
            return localAccount;
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao criar conta local", e);
        }
    }

    @Override
    public LocalAccount update(LocalAccount localAccount) {
        String sql = "UPDATE local_accounts SET email = ?, password_hash = ?, last_login_at = ?, updated_at = ? WHERE id = ?";

        int rowsAffected;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, localAccount.email());
            stmt.setString(2, localAccount.passwordHash());
            stmt.setObject(3, localAccount.lastLoginAt());
            stmt.setObject(4, localAccount.updatedAt());
            stmt.setString(5, localAccount.id());

            rowsAffected = stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao atualizar conta local", e);
        }

        if (rowsAffected == 0) {
            throw new NotFoundException("Conta local não encontrada: " + localAccount.id());
        }

        logger.debug("✓ LocalAccount updated with id: {}", localAccount.id());
        return localAccount;
    }

    @Override
    public Optional<LocalAccount> findById(String id) {
        String sql = "SELECT id, user_id, email, password_hash, last_login_at, created_at, updated_at " +
                     "FROM local_accounts WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToLocalAccount(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao buscar conta local por id: " + id, e);
        }
    }

    @Override
    public Optional<LocalAccount> findByEmail(String email) {
        String sql = "SELECT id, user_id, email, password_hash, last_login_at, created_at, updated_at " +
                     "FROM local_accounts WHERE email = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToLocalAccount(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao buscar conta local por email", e);
        }
    }

    @Override
    public Optional<LocalAccount> findByUserId(String userId) {
        String sql = "SELECT id, user_id, email, password_hash, last_login_at, created_at, updated_at " +
                     "FROM local_accounts WHERE user_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToLocalAccount(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao buscar conta local por userId: " + userId, e);
        }
    }

    @Override
    public boolean emailExists(String email) {
        String sql = "SELECT 1 FROM local_accounts WHERE email = ? LIMIT 1";

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
        String sql = "DELETE FROM local_accounts WHERE id = ?";

        int rowsAffected;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            rowsAffected = stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao deletar conta local", e);
        }

        boolean deleted = rowsAffected > 0;
        if (deleted) {
            logger.debug("✓ LocalAccount deleted with id: {}", id);
        }
        return deleted;
    }

    @Override
    public boolean deleteByUserId(String userId) {
        String sql = "DELETE FROM local_accounts WHERE user_id = ?";

        int rowsAffected;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, userId);
            rowsAffected = stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao deletar contas locais por userId: " + userId, e);
        }

        boolean deleted = rowsAffected > 0;
        if (deleted) {
            logger.debug("✓ LocalAccount(s) deleted for user_id: {}", userId);
        }
        return deleted;
    }

    private LocalAccount mapResultSetToLocalAccount(ResultSet rs) throws SQLException {
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
