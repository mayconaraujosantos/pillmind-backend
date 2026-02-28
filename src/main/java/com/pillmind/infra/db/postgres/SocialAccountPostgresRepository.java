package com.pillmind.infra.db.postgres;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pillmind.data.protocols.db.SocialAccountRepository;
import com.pillmind.domain.models.SocialAccount;

/**
 * Implementação do repositório de SocialAccount usando PostgreSQL
 */
public class SocialAccountPostgresRepository extends PostgresRepository implements SocialAccountRepository {
    private static final Logger logger = LoggerFactory.getLogger(SocialAccountPostgresRepository.class);

    public SocialAccountPostgresRepository(Connection connection) {
        super(connection);
    }

    @Override
    public SocialAccount add(SocialAccount socialAccount) {
        String sql = """
            INSERT INTO social_accounts (id, user_id, provider, provider_user_id, email, name, 
                                       profile_image_url, access_token, refresh_token, token_expiry, 
                                       linked_at, is_primary) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            String id = socialAccount.id() != null ? socialAccount.id() : UUID.randomUUID().toString();
            
            stmt.setString(1, id);
            stmt.setString(2, socialAccount.userId());
            stmt.setString(3, socialAccount.provider());
            stmt.setString(4, socialAccount.providerUserId());
            stmt.setString(5, socialAccount.email());
            stmt.setString(6, socialAccount.name());
            stmt.setString(7, socialAccount.profileImageUrl());
            stmt.setString(8, socialAccount.accessToken());
            stmt.setString(9, socialAccount.refreshToken());
            stmt.setObject(10, socialAccount.tokenExpiry());
            stmt.setObject(11, socialAccount.linkedAt());
            stmt.setBoolean(12, socialAccount.isPrimary());

            stmt.executeUpdate();
            
            return new SocialAccount(id, socialAccount.userId(), socialAccount.provider(),
                                   socialAccount.providerUserId(), socialAccount.email(), socialAccount.name(),
                                   socialAccount.profileImageUrl(), socialAccount.accessToken(), 
                                   socialAccount.refreshToken(), socialAccount.tokenExpiry(),
                                   socialAccount.linkedAt(), socialAccount.isPrimary());
        } catch (SQLException e) {
            logger.error("Error adding social account for user {}: {}", socialAccount.userId(), e.getMessage(), e);
            throw new RuntimeException("Error adding social account: " + e.getMessage(), e);
        }
    }

    @Override
    public SocialAccount update(SocialAccount socialAccount) {
        String sql = """
            UPDATE social_accounts 
            SET email = ?, name = ?, profile_image_url = ?, access_token = ?, 
                refresh_token = ?, token_expiry = ?, is_primary = ?
            WHERE id = ?::uuid
            """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, socialAccount.email());
            stmt.setString(2, socialAccount.name());
            stmt.setString(3, socialAccount.profileImageUrl());
            stmt.setString(4, socialAccount.accessToken());
            stmt.setString(5, socialAccount.refreshToken());
            stmt.setObject(6, socialAccount.tokenExpiry());
            stmt.setBoolean(7, socialAccount.isPrimary());
            stmt.setString(8, socialAccount.id());

            int updated = stmt.executeUpdate();
            if (updated == 0) {
                throw new RuntimeException("Social account not found: " + socialAccount.id());
            }

            return socialAccount;
        } catch (SQLException e) {
            logger.error("Error updating social account {}: {}", socialAccount.id(), e.getMessage(), e);
            throw new RuntimeException("Error updating social account: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<SocialAccount> loadById(String id) {
        String sql = """
            SELECT id, user_id, provider, provider_user_id, email, name, 
                   profile_image_url, access_token, refresh_token, token_expiry, 
                   linked_at, is_primary
            FROM social_accounts WHERE id = ?::uuid
            """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToSocialAccount(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            logger.error("Error loading social account by id {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error loading social account: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<SocialAccount> loadByUserAndProvider(String userId, String provider) {
        String sql = """
            SELECT id, user_id, provider, provider_user_id, email, name, 
                   profile_image_url, access_token, refresh_token, token_expiry, 
                   linked_at, is_primary
            FROM social_accounts WHERE user_id = ? AND provider = ?
            """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, userId);
            stmt.setString(2, provider);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToSocialAccount(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            logger.error("Error loading social account by user {} and provider {}: {}", 
                        userId, provider, e.getMessage(), e);
            throw new RuntimeException("Error loading social account: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<SocialAccount> loadByProviderAndProviderUserId(String provider, String providerUserId) {
        String sql = """
            SELECT id, user_id, provider, provider_user_id, email, name, 
                   profile_image_url, access_token, refresh_token, token_expiry, 
                   linked_at, is_primary
            FROM social_accounts WHERE provider = ? AND provider_user_id = ?
            """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, provider);
            stmt.setString(2, providerUserId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToSocialAccount(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            logger.error("Error loading social account by provider {} and provider user id {}: {}", 
                        provider, providerUserId, e.getMessage(), e);
            throw new RuntimeException("Error loading social account: " + e.getMessage(), e);
        }
    }

    @Override
    public List<SocialAccount> loadByUserId(String userId) {
        String sql = """
            SELECT id, user_id, provider, provider_user_id, email, name, 
                   profile_image_url, access_token, refresh_token, token_expiry, 
                   linked_at, is_primary
            FROM social_accounts WHERE user_id = ?
            ORDER BY is_primary DESC, linked_at ASC
            """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                List<SocialAccount> socialAccounts = new ArrayList<>();
                while (rs.next()) {
                    socialAccounts.add(mapResultSetToSocialAccount(rs));
                }
                return socialAccounts;
            }
        } catch (SQLException e) {
            logger.error("Error loading social accounts by user id {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Error loading social accounts: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<SocialAccount> loadPrimaryByUserId(String userId) {
        String sql = """
            SELECT id, user_id, provider, provider_user_id, email, name, 
                   profile_image_url, access_token, refresh_token, token_expiry, 
                   linked_at, is_primary
            FROM social_accounts WHERE user_id = ? AND is_primary = true
            """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToSocialAccount(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            logger.error("Error loading primary social account for user {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Error loading primary social account: " + e.getMessage(), e);
        }
    }

    @Override
    public void setPrimary(String socialAccountId) {
        String getUserIdSql = "SELECT user_id FROM social_accounts WHERE id = ?::uuid";
        String clearPrimarySql = "UPDATE social_accounts SET is_primary = false WHERE user_id = ?";
        String setPrimarySql = "UPDATE social_accounts SET is_primary = true WHERE id = ?::uuid";

        try {
            connection.setAutoCommit(false);

            // Get user_id first
            String userId;
            try (PreparedStatement stmt = connection.prepareStatement(getUserIdSql)) {
                stmt.setString(1, socialAccountId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (!rs.next()) {
                        throw new RuntimeException("Social account not found: " + socialAccountId);
                    }
                    userId = rs.getString("user_id");
                }
            }

            // Clear all primary flags for this user
            try (PreparedStatement stmt = connection.prepareStatement(clearPrimarySql)) {
                stmt.setString(1, userId);
                stmt.executeUpdate();
            }

            // Set the specified account as primary
            try (PreparedStatement stmt = connection.prepareStatement(setPrimarySql)) {
                stmt.setString(1, socialAccountId);
                int updated = stmt.executeUpdate();
                if (updated == 0) {
                    throw new RuntimeException("Social account not found: " + socialAccountId);
                }
            }

            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                logger.error("Error rolling back transaction: {}", rollbackEx.getMessage());
            }
            logger.error("Error setting primary social account {}: {}", socialAccountId, e.getMessage(), e);
            throw new RuntimeException("Error setting primary social account: " + e.getMessage(), e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                logger.error("Error resetting auto-commit: {}", e.getMessage());
            }
        }
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM social_accounts WHERE id = ?::uuid";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            int deleted = stmt.executeUpdate();
            if (deleted == 0) {
                throw new RuntimeException("Social account not found: " + id);
            }
        } catch (SQLException e) {
            logger.error("Error deleting social account {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error deleting social account: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteByUserId(String userId) {
        String sql = "DELETE FROM social_accounts WHERE user_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error deleting social accounts for user {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Error deleting social accounts: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existsByUserAndProvider(String userId, String provider) {
        String sql = "SELECT 1 FROM social_accounts WHERE user_id = ? AND provider = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, userId);
            stmt.setString(2, provider);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            logger.error("Error checking social account existence for user {} and provider {}: {}", 
                        userId, provider, e.getMessage(), e);
            throw new RuntimeException("Error checking social account existence: " + e.getMessage(), e);
        }
    }

    @Override
    public List<SocialAccount> loadByProvider(String provider) {
        String sql = """
            SELECT id, user_id, provider, provider_user_id, email, name, 
                   profile_image_url, access_token, refresh_token, token_expiry, 
                   linked_at, is_primary
            FROM social_accounts WHERE provider = ?
            ORDER BY linked_at DESC
            """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, provider);

            try (ResultSet rs = stmt.executeQuery()) {
                List<SocialAccount> socialAccounts = new ArrayList<>();
                while (rs.next()) {
                    socialAccounts.add(mapResultSetToSocialAccount(rs));
                }
                return socialAccounts;
            }
        } catch (SQLException e) {
            logger.error("Error loading social accounts by provider {}: {}", provider, e.getMessage(), e);
            throw new RuntimeException("Error loading social accounts: " + e.getMessage(), e);
        }
    }

    private SocialAccount mapResultSetToSocialAccount(ResultSet rs) throws SQLException {
        return new SocialAccount(
            rs.getString("id"),
            rs.getString("user_id"),
            rs.getString("provider"),
            rs.getString("provider_user_id"),
            rs.getString("email"),
            rs.getString("name"),
            rs.getString("profile_image_url"),
            rs.getString("access_token"),
            rs.getString("refresh_token"),
            rs.getObject("token_expiry", LocalDateTime.class),
            rs.getObject("linked_at", LocalDateTime.class),
            rs.getBoolean("is_primary")
        );
    }
}