package com.pillmind.infra.db.postgres;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pillmind.data.protocols.db.LocalAccountRepository;
import com.pillmind.domain.models.LocalAccount;

/**
 * Implementação do repositório de LocalAccount usando PostgreSQL
 */
public class LocalAccountPostgresRepository extends PostgresRepository implements LocalAccountRepository {
    private static final Logger logger = LoggerFactory.getLogger(LocalAccountPostgresRepository.class);

    public LocalAccountPostgresRepository(Connection connection) {
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
            logger.error("Error adding local account with email {}: {}", localAccount.email(), e.getMessage(), e);
            throw new RuntimeException("Erro interno do servidor ao criar conta local", e);
        }
    }

    @Override
    public LocalAccount update(LocalAccount localAccount) {
        String sql = "UPDATE local_accounts SET email = ?, password_hash = ?, last_login_at = ?, updated_at = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, localAccount.email());
            stmt.setString(2, localAccount.passwordHash());
            stmt.setObject(3, localAccount.lastLoginAt());
            stmt.setObject(4, localAccount.updatedAt());
            stmt.setString(5, localAccount.id());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                logger.warn("No local account found with id: {}", localAccount.id());
                throw new RuntimeException("Conta local não encontrada");
            }

            logger.debug("✓ LocalAccount updated with id: {}", localAccount.id());
            return localAccount;
        } catch (SQLException e) {
            logger.error("Error updating local account with id {}: {}", localAccount.id(), e.getMessage(), e);
            throw new RuntimeException("Erro interno do servidor ao atualizar conta local", e);
        }
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
            logger.error("Error finding local account by id {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Erro interno do servidor ao buscar conta local", e);
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
            logger.error("Error finding local account by email {}: {}", email, e.getMessage(), e);
            throw new RuntimeException("Erro interno do servidor ao buscar conta local", e);
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
            logger.error("Error finding local account by user_id {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Erro interno do servidor ao buscar conta local", e);
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
            logger.error("Error checking if email exists in local accounts {}: {}", email, e.getMessage(), e);
            throw new RuntimeException("Erro interno do servidor ao verificar email", e);
        }
    }

    @Override
    public boolean delete(String id) {
        String sql = "DELETE FROM local_accounts WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);

            int rowsAffected = stmt.executeUpdate();
            boolean deleted = rowsAffected > 0;
            
            if (deleted) {
                logger.debug("✓ LocalAccount deleted with id: {}", id);
            } else {
                logger.warn("No local account found for deletion with id: {}", id);
            }
            
            return deleted;
        } catch (SQLException e) {
            logger.error("Error deleting local account with id {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Erro interno do servidor ao deletar conta local", e);
        }
    }

    @Override
    public boolean deleteByUserId(String userId) {
        String sql = "DELETE FROM local_accounts WHERE user_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, userId);

            int rowsAffected = stmt.executeUpdate();
            boolean deleted = rowsAffected > 0;
            
            if (deleted) {
                logger.debug("✓ LocalAccount(s) deleted for user_id: {}", userId);
            } else {
                logger.warn("No local accounts found for deletion with user_id: {}", userId);
            }
            
            return deleted;
        } catch (SQLException e) {
            logger.error("Error deleting local accounts for user_id {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Erro interno do servidor ao deletar contas locais", e);
        }
    }

    /**
     * Mapeia ResultSet para entidade LocalAccount
     */
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