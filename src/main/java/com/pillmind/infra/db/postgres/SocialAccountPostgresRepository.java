package com.pillmind.infra.db.postgres;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.JdbiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pillmind.data.protocols.db.SocialAccountRepository;
import com.pillmind.domain.errors.DatabaseException;
import com.pillmind.domain.errors.NotFoundException;
import com.pillmind.domain.models.SocialAccount;

@SuppressWarnings("java:S2139")
public class SocialAccountPostgresRepository extends PostgresRepository implements SocialAccountRepository {
    private static final Logger logger = LoggerFactory.getLogger(SocialAccountPostgresRepository.class);

    private static final String SELECT_COLUMNS = """
            SELECT id, user_id, provider, provider_user_id, email, name,
                   profile_image_url, access_token, refresh_token, token_expiry,
                   linked_at, is_primary
            FROM social_accounts
            """;

    public SocialAccountPostgresRepository(Jdbi jdbi) {
        super(jdbi);
    }

    @Override
    public SocialAccount add(SocialAccount socialAccount) {
        String id = socialAccount.id() != null ? socialAccount.id() : UUID.randomUUID().toString();
        try {
            jdbi.useHandle(h -> h.createUpdate("""
                    INSERT INTO social_accounts (id, user_id, provider, provider_user_id, email, name,
                                               profile_image_url, access_token, refresh_token, token_expiry,
                                               linked_at, is_primary)
                    VALUES (:id, :userId, :provider, :providerUserId, :email, :name,
                            :profileImageUrl, :accessToken, :refreshToken, :tokenExpiry,
                            :linkedAt, :isPrimary)
                    """)
                .bind("id", id)
                .bind("userId", socialAccount.userId())
                .bind("provider", socialAccount.provider())
                .bind("providerUserId", socialAccount.providerUserId())
                .bind("email", socialAccount.email())
                .bind("name", socialAccount.name())
                .bind("profileImageUrl", socialAccount.profileImageUrl())
                .bind("accessToken", socialAccount.accessToken())
                .bind("refreshToken", socialAccount.refreshToken())
                .bind("tokenExpiry", socialAccount.tokenExpiry())
                .bind("linkedAt", socialAccount.linkedAt())
                .bind("isPrimary", socialAccount.isPrimary())
                .execute());
            return new SocialAccount(id, socialAccount.userId(), socialAccount.provider(),
                    socialAccount.providerUserId(), socialAccount.email(), socialAccount.name(),
                    socialAccount.profileImageUrl(), socialAccount.accessToken(),
                    socialAccount.refreshToken(), socialAccount.tokenExpiry(),
                    socialAccount.linkedAt(), socialAccount.isPrimary());
        } catch (JdbiException e) {
            throw new DatabaseException("Erro ao criar social account para userId: " + socialAccount.userId(), e);
        }
    }

    @Override
    public SocialAccount update(SocialAccount socialAccount) {
        try {
            int updated = jdbi.withHandle(h -> h.createUpdate("""
                    UPDATE social_accounts
                    SET email = :email, name = :name, profile_image_url = :profileImageUrl,
                        access_token = :accessToken, refresh_token = :refreshToken,
                        token_expiry = :tokenExpiry, is_primary = :isPrimary
                    WHERE id = :id::uuid
                    """)
                .bind("email", socialAccount.email())
                .bind("name", socialAccount.name())
                .bind("profileImageUrl", socialAccount.profileImageUrl())
                .bind("accessToken", socialAccount.accessToken())
                .bind("refreshToken", socialAccount.refreshToken())
                .bind("tokenExpiry", socialAccount.tokenExpiry())
                .bind("isPrimary", socialAccount.isPrimary())
                .bind("id", socialAccount.id())
                .execute());

            if (updated == 0) {
                throw new NotFoundException("Social account não encontrada: " + socialAccount.id());
            }
            return socialAccount;
        } catch (JdbiException e) {
            throw new DatabaseException("Erro ao atualizar social account: " + socialAccount.id(), e);
        }
    }

    @Override
    public Optional<SocialAccount> loadById(String id) {
        try {
            return jdbi.withHandle(h -> h.createQuery(SELECT_COLUMNS + "WHERE id = :id::uuid")
                .bind("id", id)
                .map((rs, ctx) -> mapRow(rs))
                .findFirst());
        } catch (JdbiException e) {
            throw new DatabaseException("Erro ao buscar social account por id: " + id, e);
        }
    }

    @Override
    public Optional<SocialAccount> loadByUserAndProvider(String userId, String provider) {
        try {
            return jdbi.withHandle(h -> h.createQuery(
                    SELECT_COLUMNS + "WHERE user_id = :userId AND provider = :provider")
                .bind("userId", userId)
                .bind("provider", provider)
                .map((rs, ctx) -> mapRow(rs))
                .findFirst());
        } catch (JdbiException e) {
            throw new DatabaseException("Erro ao buscar social account por userId e provider", e);
        }
    }

    @Override
    public Optional<SocialAccount> loadByProviderAndProviderUserId(String provider, String providerUserId) {
        try {
            return jdbi.withHandle(h -> h.createQuery(
                    SELECT_COLUMNS + "WHERE provider = :provider AND provider_user_id = :providerUserId")
                .bind("provider", provider)
                .bind("providerUserId", providerUserId)
                .map((rs, ctx) -> mapRow(rs))
                .findFirst());
        } catch (JdbiException e) {
            throw new DatabaseException("Erro ao buscar social account por provider e providerUserId", e);
        }
    }

    @Override
    public List<SocialAccount> loadByUserId(String userId) {
        try {
            return jdbi.withHandle(h -> h.createQuery(
                    SELECT_COLUMNS + "WHERE user_id = :userId ORDER BY is_primary DESC, linked_at ASC")
                .bind("userId", userId)
                .map((rs, ctx) -> mapRow(rs))
                .list());
        } catch (JdbiException e) {
            throw new DatabaseException("Erro ao buscar social accounts por userId: " + userId, e);
        }
    }

    @Override
    public Optional<SocialAccount> loadPrimaryByUserId(String userId) {
        try {
            return jdbi.withHandle(h -> h.createQuery(
                    SELECT_COLUMNS + "WHERE user_id = :userId AND is_primary = true")
                .bind("userId", userId)
                .map((rs, ctx) -> mapRow(rs))
                .findFirst());
        } catch (JdbiException e) {
            throw new DatabaseException("Erro ao buscar social account primária por userId: " + userId, e);
        }
    }

    @Override
    public void setPrimary(String socialAccountId) {
        try {
            jdbi.useTransaction(h -> {
                String userId = h.createQuery(
                        "SELECT user_id FROM social_accounts WHERE id = :id::uuid")
                        .bind("id", socialAccountId)
                        .mapTo(String.class)
                        .findFirst()
                        .orElseThrow(() -> new NotFoundException("Social account não encontrada: " + socialAccountId));

                h.createUpdate("UPDATE social_accounts SET is_primary = false WHERE user_id = :userId")
                        .bind("userId", userId)
                        .execute();

                int updated = h.createUpdate(
                        "UPDATE social_accounts SET is_primary = true WHERE id = :id::uuid")
                        .bind("id", socialAccountId)
                        .execute();

                if (updated == 0) {
                    throw new NotFoundException("Social account não encontrada: " + socialAccountId);
                }
            });
        } catch (JdbiException e) {
            throw new DatabaseException("Erro ao definir social account como primária: " + socialAccountId, e);
        }
    }

    @Override
    public void delete(String id) {
        try {
            int deleted = jdbi.withHandle(h -> h.createUpdate(
                    "DELETE FROM social_accounts WHERE id = :id::uuid")
                .bind("id", id)
                .execute());
            if (deleted == 0) {
                throw new NotFoundException("Social account não encontrada: " + id);
            }
        } catch (JdbiException e) {
            throw new DatabaseException("Erro ao deletar social account: " + id, e);
        }
    }

    @Override
    public void deleteByUserId(String userId) {
        try {
            jdbi.useHandle(h -> h.createUpdate(
                    "DELETE FROM social_accounts WHERE user_id = :userId")
                .bind("userId", userId)
                .execute());
        } catch (JdbiException e) {
            throw new DatabaseException("Erro ao deletar social accounts por userId: " + userId, e);
        }
    }

    @Override
    public boolean existsByUserAndProvider(String userId, String provider) {
        try {
            return jdbi.withHandle(h -> h.createQuery(
                    "SELECT 1 FROM social_accounts WHERE user_id = :userId AND provider = :provider")
                .bind("userId", userId)
                .bind("provider", provider)
                .mapTo(Integer.class)
                .findFirst()
                .isPresent());
        } catch (JdbiException e) {
            throw new DatabaseException("Erro ao verificar existência de social account", e);
        }
    }

    @Override
    public List<SocialAccount> loadByProvider(String provider) {
        try {
            return jdbi.withHandle(h -> h.createQuery(
                    SELECT_COLUMNS + "WHERE provider = :provider ORDER BY linked_at DESC")
                .bind("provider", provider)
                .map((rs, ctx) -> mapRow(rs))
                .list());
        } catch (JdbiException e) {
            throw new DatabaseException("Erro ao buscar social accounts por provider: " + provider, e);
        }
    }

    private SocialAccount mapRow(java.sql.ResultSet rs) throws java.sql.SQLException {
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
