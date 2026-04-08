package com.pillmind.infra.db.mariadb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pillmind.data.protocols.db.OAuthAccountRepository;
import com.pillmind.domain.errors.DatabaseException;
import com.pillmind.domain.errors.NotFoundException;
import com.pillmind.domain.models.AuthProvider;
import com.pillmind.domain.models.OAuthAccount;

@SuppressWarnings("java:S2139")
public class OAuthAccountMariaDbRepository extends MariaDbRepository implements OAuthAccountRepository {
    private static final Logger logger = LoggerFactory.getLogger(OAuthAccountMariaDbRepository.class);

    private static final String SELECT_COLUMNS =
        "SELECT id, user_id, provider, provider_user_id, email, provider_name, profile_image_url, " +
        "access_token, refresh_token, token_expiry, last_login_at, linked_at, is_primary, created_at, updated_at ";

    public OAuthAccountMariaDbRepository(Connection connection) {
        super(connection);
    }

    @Override
    public OAuthAccount add(OAuthAccount oauthAccount) {
        String sql = "INSERT INTO oauth_accounts (id, user_id, provider, provider_user_id, email, provider_name, " +
                     "profile_image_url, access_token, refresh_token, token_expiry, last_login_at, linked_at, " +
                     "is_primary, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, oauthAccount.id());
            stmt.setString(2, oauthAccount.userId());
            stmt.setString(3, oauthAccount.provider().getValue());
            stmt.setString(4, oauthAccount.providerUserId());
            stmt.setString(5, oauthAccount.email());
            stmt.setString(6, oauthAccount.providerName());
            stmt.setString(7, oauthAccount.profileImageUrl());
            stmt.setString(8, oauthAccount.accessToken());
            stmt.setString(9, oauthAccount.refreshToken());
            stmt.setObject(10, oauthAccount.tokenExpiry());
            stmt.setObject(11, oauthAccount.lastLoginAt());
            stmt.setObject(12, oauthAccount.linkedAt());
            stmt.setBoolean(13, oauthAccount.isPrimary());
            stmt.setObject(14, oauthAccount.createdAt());
            stmt.setObject(15, oauthAccount.updatedAt());

            stmt.executeUpdate();
            logger.debug("✓ OAuthAccount created with id: {}", oauthAccount.id());
            return oauthAccount;
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao criar conta OAuth", e);
        }
    }

    @Override
    public OAuthAccount update(OAuthAccount oauthAccount) {
        String sql = "UPDATE oauth_accounts SET email = ?, provider_name = ?, profile_image_url = ?, " +
                     "access_token = ?, refresh_token = ?, token_expiry = ?, last_login_at = ?, " +
                     "is_primary = ?, updated_at = ? WHERE id = ?";

        int rowsAffected;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, oauthAccount.email());
            stmt.setString(2, oauthAccount.providerName());
            stmt.setString(3, oauthAccount.profileImageUrl());
            stmt.setString(4, oauthAccount.accessToken());
            stmt.setString(5, oauthAccount.refreshToken());
            stmt.setObject(6, oauthAccount.tokenExpiry());
            stmt.setObject(7, oauthAccount.lastLoginAt());
            stmt.setBoolean(8, oauthAccount.isPrimary());
            stmt.setObject(9, oauthAccount.updatedAt());
            stmt.setString(10, oauthAccount.id());

            rowsAffected = stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao atualizar conta OAuth", e);
        }

        if (rowsAffected == 0) {
            throw new NotFoundException("Conta OAuth não encontrada: " + oauthAccount.id());
        }

        logger.debug("✓ OAuthAccount updated with id: {}", oauthAccount.id());
        return oauthAccount;
    }

    @Override
    public Optional<OAuthAccount> findById(String id) {
        String sql = SELECT_COLUMNS + "FROM oauth_accounts WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToOAuthAccount(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao buscar conta OAuth por id: " + id, e);
        }
    }

    @Override
    public Optional<OAuthAccount> findByProviderAndProviderUserId(AuthProvider provider, String providerUserId) {
        String sql = SELECT_COLUMNS + "FROM oauth_accounts WHERE provider = ? AND provider_user_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, provider.getValue());
            stmt.setString(2, providerUserId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToOAuthAccount(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao buscar conta OAuth por provider e providerUserId", e);
        }
    }

    @Override
    public List<OAuthAccount> findByUserId(String userId) {
        String sql = SELECT_COLUMNS + "FROM oauth_accounts WHERE user_id = ? ORDER BY is_primary DESC, linked_at DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                List<OAuthAccount> accounts = new ArrayList<>();
                while (rs.next()) {
                    accounts.add(mapResultSetToOAuthAccount(rs));
                }
                return accounts;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao buscar contas OAuth por userId: " + userId, e);
        }
    }

    @Override
    public Optional<OAuthAccount> findPrimaryByUserId(String userId) {
        String sql = SELECT_COLUMNS + "FROM oauth_accounts WHERE user_id = ? AND is_primary = true LIMIT 1";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToOAuthAccount(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao buscar conta OAuth primária por userId: " + userId, e);
        }
    }

    @Override
    public List<OAuthAccount> findByUserIdAndProvider(String userId, AuthProvider provider) {
        String sql = SELECT_COLUMNS + "FROM oauth_accounts WHERE user_id = ? AND provider = ? ORDER BY is_primary DESC, linked_at DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, userId);
            stmt.setString(2, provider.getValue());

            try (ResultSet rs = stmt.executeQuery()) {
                List<OAuthAccount> accounts = new ArrayList<>();
                while (rs.next()) {
                    accounts.add(mapResultSetToOAuthAccount(rs));
                }
                return accounts;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao buscar contas OAuth por userId e provider", e);
        }
    }

    @Override
    public void clearPrimaryByUserId(String userId) {
        String sql = "UPDATE oauth_accounts SET is_primary = false, updated_at = CURRENT_TIMESTAMP WHERE user_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, userId);
            stmt.executeUpdate();
            logger.debug("✓ Cleared primary status for all oauth accounts of user_id: {}", userId);
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao limpar status primário de contas OAuth", e);
        }
    }

    @Override
    public boolean delete(String id) {
        String sql = "DELETE FROM oauth_accounts WHERE id = ?";

        int rowsAffected;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            rowsAffected = stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao deletar conta OAuth", e);
        }

        boolean deleted = rowsAffected > 0;
        if (deleted) {
            logger.debug("✓ OAuthAccount deleted with id: {}", id);
        }
        return deleted;
    }

    @Override
    public boolean deleteByUserId(String userId) {
        String sql = "DELETE FROM oauth_accounts WHERE user_id = ?";

        int rowsAffected;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, userId);
            rowsAffected = stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao deletar contas OAuth por userId: " + userId, e);
        }

        boolean deleted = rowsAffected > 0;
        if (deleted) {
            logger.debug("✓ OAuthAccount(s) deleted for user_id: {}", userId);
        }
        return deleted;
    }

    @Override
    public long countByUserId(String userId) {
        String sql = "SELECT COUNT(*) FROM oauth_accounts WHERE user_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
                return 0;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao contar contas OAuth por userId: " + userId, e);
        }
    }

    private OAuthAccount mapResultSetToOAuthAccount(ResultSet rs) throws SQLException {
        return new OAuthAccount(
            rs.getString("id"),
            rs.getString("user_id"),
            AuthProvider.fromString(rs.getString("provider")),
            rs.getString("provider_user_id"),
            rs.getString("email"),
            rs.getString("provider_name"),
            rs.getString("profile_image_url"),
            rs.getString("access_token"),
            rs.getString("refresh_token"),
            rs.getTimestamp("token_expiry") != null ? rs.getTimestamp("token_expiry").toLocalDateTime() : null,
            rs.getTimestamp("last_login_at") != null ? rs.getTimestamp("last_login_at").toLocalDateTime() : null,
            rs.getTimestamp("linked_at").toLocalDateTime(),
            rs.getBoolean("is_primary"),
            rs.getTimestamp("created_at").toLocalDateTime(),
            rs.getTimestamp("updated_at").toLocalDateTime()
        );
    }
}
