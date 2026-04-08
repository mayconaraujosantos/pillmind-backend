package com.pillmind.infra.db.postgres;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.JdbiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pillmind.data.protocols.db.OAuthAccountRepository;
import com.pillmind.domain.errors.DatabaseException;
import com.pillmind.domain.errors.NotFoundException;
import com.pillmind.domain.models.AuthProvider;
import com.pillmind.domain.models.OAuthAccount;

public class OAuthAccountPostgresRepository extends PostgresRepository implements OAuthAccountRepository {
    private static final Logger logger = LoggerFactory.getLogger(OAuthAccountPostgresRepository.class);

    private static final String SELECT_COLUMNS =
        "SELECT id, user_id, provider, provider_user_id, email, provider_name, profile_image_url, " +
        "access_token, refresh_token, token_expiry, last_login_at, linked_at, is_primary, created_at, updated_at ";

    public OAuthAccountPostgresRepository(Jdbi jdbi) {
        super(jdbi);
    }

    @Override
    public OAuthAccount add(OAuthAccount oauthAccount) {
        try {
            jdbi.useHandle(h -> h.createUpdate(
                    "INSERT INTO oauth_accounts (id, user_id, provider, provider_user_id, email, provider_name, " +
                    "profile_image_url, access_token, refresh_token, token_expiry, last_login_at, linked_at, " +
                    "is_primary, created_at, updated_at) " +
                    "VALUES (:id, :userId, :provider, :providerUserId, :email, :providerName, " +
                    ":profileImageUrl, :accessToken, :refreshToken, :tokenExpiry, :lastLoginAt, :linkedAt, " +
                    ":isPrimary, :createdAt, :updatedAt)")
                .bind("id", oauthAccount.id())
                .bind("userId", oauthAccount.userId())
                .bind("provider", oauthAccount.provider().getValue())
                .bind("providerUserId", oauthAccount.providerUserId())
                .bind("email", oauthAccount.email())
                .bind("providerName", oauthAccount.providerName())
                .bind("profileImageUrl", oauthAccount.profileImageUrl())
                .bind("accessToken", oauthAccount.accessToken())
                .bind("refreshToken", oauthAccount.refreshToken())
                .bind("tokenExpiry", oauthAccount.tokenExpiry() != null ? Timestamp.valueOf(oauthAccount.tokenExpiry()) : null)
                .bind("lastLoginAt", oauthAccount.lastLoginAt() != null ? Timestamp.valueOf(oauthAccount.lastLoginAt()) : null)
                .bind("linkedAt", Timestamp.valueOf(oauthAccount.linkedAt()))
                .bind("isPrimary", oauthAccount.isPrimary())
                .bind("createdAt", Timestamp.valueOf(oauthAccount.createdAt()))
                .bind("updatedAt", Timestamp.valueOf(oauthAccount.updatedAt()))
                .execute());
            logger.debug("✓ OAuthAccount created with id: {}", oauthAccount.id());
            return oauthAccount;
        } catch (JdbiException e) {
            throw new DatabaseException("Erro ao criar conta OAuth", e);
        }
    }

    @Override
    public OAuthAccount update(OAuthAccount oauthAccount) {
        try {
            int rowsAffected = jdbi.withHandle(h -> h.createUpdate(
                    "UPDATE oauth_accounts SET email = :email, provider_name = :providerName, " +
                    "profile_image_url = :profileImageUrl, access_token = :accessToken, refresh_token = :refreshToken, " +
                    "token_expiry = :tokenExpiry, last_login_at = :lastLoginAt, is_primary = :isPrimary, " +
                    "updated_at = :updatedAt WHERE id = :id")
                .bind("email", oauthAccount.email())
                .bind("providerName", oauthAccount.providerName())
                .bind("profileImageUrl", oauthAccount.profileImageUrl())
                .bind("accessToken", oauthAccount.accessToken())
                .bind("refreshToken", oauthAccount.refreshToken())
                .bind("tokenExpiry", oauthAccount.tokenExpiry() != null ? Timestamp.valueOf(oauthAccount.tokenExpiry()) : null)
                .bind("lastLoginAt", oauthAccount.lastLoginAt() != null ? Timestamp.valueOf(oauthAccount.lastLoginAt()) : null)
                .bind("isPrimary", oauthAccount.isPrimary())
                .bind("updatedAt", Timestamp.valueOf(oauthAccount.updatedAt()))
                .bind("id", oauthAccount.id())
                .execute());

            if (rowsAffected == 0) {
                throw new NotFoundException("Conta OAuth não encontrada: " + oauthAccount.id());
            }
            logger.debug("✓ OAuthAccount updated with id: {}", oauthAccount.id());
            return oauthAccount;
        } catch (JdbiException e) {
            throw new DatabaseException("Erro ao atualizar conta OAuth", e);
        }
    }

    @Override
    public Optional<OAuthAccount> findById(String id) {
        try {
            return jdbi.withHandle(h -> h.createQuery(SELECT_COLUMNS + "FROM oauth_accounts WHERE id = :id")
                .bind("id", id)
                .map((rs, ctx) -> mapRow(rs))
                .findFirst());
        } catch (JdbiException e) {
            throw new DatabaseException("Erro ao buscar conta OAuth por id: " + id, e);
        }
    }

    @Override
    public Optional<OAuthAccount> findByProviderAndProviderUserId(AuthProvider provider, String providerUserId) {
        try {
            return jdbi.withHandle(h -> h.createQuery(
                    SELECT_COLUMNS + "FROM oauth_accounts WHERE provider = :provider AND provider_user_id = :providerUserId")
                .bind("provider", provider.getValue())
                .bind("providerUserId", providerUserId)
                .map((rs, ctx) -> mapRow(rs))
                .findFirst());
        } catch (JdbiException e) {
            throw new DatabaseException("Erro ao buscar conta OAuth por provider e providerUserId", e);
        }
    }

    @Override
    public List<OAuthAccount> findByUserId(String userId) {
        try {
            return jdbi.withHandle(h -> h.createQuery(
                    SELECT_COLUMNS + "FROM oauth_accounts WHERE user_id = :userId ORDER BY is_primary DESC, linked_at DESC")
                .bind("userId", userId)
                .map((rs, ctx) -> mapRow(rs))
                .list());
        } catch (JdbiException e) {
            throw new DatabaseException("Erro ao buscar contas OAuth por userId: " + userId, e);
        }
    }

    @Override
    public Optional<OAuthAccount> findPrimaryByUserId(String userId) {
        try {
            return jdbi.withHandle(h -> h.createQuery(
                    SELECT_COLUMNS + "FROM oauth_accounts WHERE user_id = :userId AND is_primary = true LIMIT 1")
                .bind("userId", userId)
                .map((rs, ctx) -> mapRow(rs))
                .findFirst());
        } catch (JdbiException e) {
            throw new DatabaseException("Erro ao buscar conta OAuth primária por userId: " + userId, e);
        }
    }

    @Override
    public List<OAuthAccount> findByUserIdAndProvider(String userId, AuthProvider provider) {
        try {
            return jdbi.withHandle(h -> h.createQuery(
                    SELECT_COLUMNS + "FROM oauth_accounts WHERE user_id = :userId AND provider = :provider ORDER BY is_primary DESC, linked_at DESC")
                .bind("userId", userId)
                .bind("provider", provider.getValue())
                .map((rs, ctx) -> mapRow(rs))
                .list());
        } catch (JdbiException e) {
            throw new DatabaseException("Erro ao buscar contas OAuth por userId e provider", e);
        }
    }

    @Override
    public void clearPrimaryByUserId(String userId) {
        try {
            jdbi.useHandle(h -> h.createUpdate(
                    "UPDATE oauth_accounts SET is_primary = false, updated_at = CURRENT_TIMESTAMP WHERE user_id = :userId")
                .bind("userId", userId)
                .execute());
            logger.debug("✓ Cleared primary status for all oauth accounts of user_id: {}", userId);
        } catch (JdbiException e) {
            throw new DatabaseException("Erro ao limpar status primário de contas OAuth", e);
        }
    }

    @Override
    public boolean delete(String id) {
        try {
            int rowsAffected = jdbi.withHandle(h -> h.createUpdate(
                    "DELETE FROM oauth_accounts WHERE id = :id")
                .bind("id", id)
                .execute());
            if (rowsAffected > 0) {
                logger.debug("✓ OAuthAccount deleted with id: {}", id);
            }
            return rowsAffected > 0;
        } catch (JdbiException e) {
            throw new DatabaseException("Erro ao deletar conta OAuth", e);
        }
    }

    @Override
    public boolean deleteByUserId(String userId) {
        try {
            int rowsAffected = jdbi.withHandle(h -> h.createUpdate(
                    "DELETE FROM oauth_accounts WHERE user_id = :userId")
                .bind("userId", userId)
                .execute());
            if (rowsAffected > 0) {
                logger.debug("✓ OAuthAccount(s) deleted for user_id: {}", userId);
            }
            return rowsAffected > 0;
        } catch (JdbiException e) {
            throw new DatabaseException("Erro ao deletar contas OAuth por userId: " + userId, e);
        }
    }

    @Override
    public long countByUserId(String userId) {
        try {
            return jdbi.withHandle(h -> h.createQuery(
                    "SELECT COUNT(*) FROM oauth_accounts WHERE user_id = :userId")
                .bind("userId", userId)
                .mapTo(Long.class)
                .one());
        } catch (JdbiException e) {
            throw new DatabaseException("Erro ao contar contas OAuth por userId: " + userId, e);
        }
    }

    private OAuthAccount mapRow(ResultSet rs) throws SQLException {
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
