package com.pillmind.infra.db.postgres;

import java.sql.Timestamp;
import java.util.Optional;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.JdbiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pillmind.data.protocols.db.AddAccountRepository;
import com.pillmind.data.protocols.db.LoadAccountByEmailRepository;
import com.pillmind.data.protocols.db.LoadAccountByIdRepository;
import com.pillmind.domain.models.Account;

@SuppressWarnings({"java:S1192", "java:S2139"})
public class AccountPostgresRepository extends PostgresRepository
    implements AddAccountRepository, LoadAccountByEmailRepository, LoadAccountByIdRepository {
  private static final Logger logger = LoggerFactory.getLogger(AccountPostgresRepository.class);

  public AccountPostgresRepository(Jdbi jdbi) {
    super(jdbi);
  }

  @Override
  public Account add(Account account) {
    try {
      jdbi.useHandle(h -> h.createUpdate(
              "INSERT INTO accounts (id, name, email, password, google_account, google_id, picture_url, " +
              "last_login_at, auth_provider, email_verified, created_at, updated_at) " +
              "VALUES (:id, :name, :email, :password, :googleAccount, :googleId, :pictureUrl, " +
              ":lastLoginAt, :authProvider, :emailVerified, :createdAt, :updatedAt)")
          .bind("id", account.id())
          .bind("name", account.name())
          .bind("email", account.email())
          .bind("password", account.password())
          .bind("googleAccount", account.googleAccount())
          .bind("googleId", account.googleId())
          .bind("pictureUrl", account.pictureUrl())
          .bind("lastLoginAt", account.lastLoginAt() != null ? Timestamp.valueOf(account.lastLoginAt()) : null)
          .bind("authProvider", account.authProvider().getValue())
          .bind("emailVerified", account.emailVerified())
          .bind("createdAt", Timestamp.valueOf(account.createdAt()))
          .bind("updatedAt", Timestamp.valueOf(account.updatedAt()))
          .execute());
      return account;
    } catch (JdbiException e) {
      logger.error("Error adding account with email {}: {}", account.email(), e.getMessage(), e);
      throw new RuntimeException("Error adding account: " + e.getMessage(), e);
    }
  }

  @Override
  public Optional<Account> loadByEmail(String email) {
    try {
      return jdbi.withHandle(h -> h.createQuery(
              "SELECT id, name, email, password, google_account, google_id, picture_url, last_login_at, " +
              "auth_provider, email_verified, created_at, updated_at FROM accounts WHERE email = :email")
          .bind("email", email)
          .map((rs, ctx) -> new Account(
              rs.getString("id"), rs.getString("name"), rs.getString("email"),
              rs.getString("password"), rs.getBoolean("google_account"), rs.getString("google_id"),
              rs.getString("picture_url"),
              rs.getTimestamp("last_login_at") != null ? rs.getTimestamp("last_login_at").toLocalDateTime() : null,
              rs.getString("auth_provider"), rs.getBoolean("email_verified"),
              rs.getTimestamp("created_at").toLocalDateTime(), rs.getTimestamp("updated_at").toLocalDateTime()))
          .findFirst());
    } catch (JdbiException e) {
      logger.error("Error loading account by email {}: {}", email, e.getMessage(), e);
      throw new RuntimeException("Error loading account by email: " + e.getMessage(), e);
    }
  }

  @Override
  public Optional<Account> loadById(String id) {
    try {
      return jdbi.withHandle(h -> h.createQuery(
              "SELECT id, name, email, password, google_account, google_id, picture_url, last_login_at, " +
              "auth_provider, email_verified, created_at, updated_at FROM accounts WHERE id = :id")
          .bind("id", id)
          .map((rs, ctx) -> new Account(
              rs.getString("id"), rs.getString("name"), rs.getString("email"),
              rs.getString("password"), rs.getBoolean("google_account"), rs.getString("google_id"),
              rs.getString("picture_url"),
              rs.getTimestamp("last_login_at") != null ? rs.getTimestamp("last_login_at").toLocalDateTime() : null,
              rs.getString("auth_provider"), rs.getBoolean("email_verified"),
              rs.getTimestamp("created_at").toLocalDateTime(), rs.getTimestamp("updated_at").toLocalDateTime()))
          .findFirst());
    } catch (JdbiException e) {
      logger.error("Error loading account by id {}: {}", id, e.getMessage(), e);
      throw new RuntimeException("Error loading account by id: " + e.getMessage(), e);
    }
  }

  @Override
  public Account update(Account account) {
    try {
      jdbi.useHandle(h -> h.createUpdate(
              "UPDATE accounts SET name = :name, google_id = :googleId, picture_url = :pictureUrl, " +
              "last_login_at = :lastLoginAt, auth_provider = :authProvider, email_verified = :emailVerified, " +
              "updated_at = :updatedAt WHERE id = :id")
          .bind("name", account.name())
          .bind("googleId", account.googleId())
          .bind("pictureUrl", account.pictureUrl())
          .bind("lastLoginAt", account.lastLoginAt() != null ? Timestamp.valueOf(account.lastLoginAt()) : null)
          .bind("authProvider", account.authProvider().getValue())
          .bind("emailVerified", account.emailVerified())
          .bind("updatedAt", Timestamp.valueOf(account.updatedAt()))
          .bind("id", account.id())
          .execute());
      return account;
    } catch (JdbiException e) {
      logger.error("Error updating account {}: {}", account.id(), e.getMessage(), e);
      throw new RuntimeException("Error updating account: " + e.getMessage(), e);
    }
  }
}
