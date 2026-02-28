package com.pillmind.infra.db.postgres;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pillmind.data.protocols.db.AddAccountRepository;
import com.pillmind.data.protocols.db.LoadAccountByEmailRepository;
import com.pillmind.data.protocols.db.LoadAccountByIdRepository;
import com.pillmind.domain.models.Account;

/**
 * Implementação do repositório de Account usando PostgreSQL
 */
public class AccountPostgresRepository extends PostgresRepository
    implements AddAccountRepository, LoadAccountByEmailRepository, LoadAccountByIdRepository {
  private static final Logger logger = LoggerFactory.getLogger(AccountPostgresRepository.class);

  public AccountPostgresRepository(Connection connection) {
    super(connection);
  }

  @Override
  public Account add(Account account) {
    String sql = "INSERT INTO accounts (id, name, email, password, google_account, google_id, picture_url, last_login_at, auth_provider, email_verified, created_at, updated_at) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setString(1, account.id());
      stmt.setString(2, account.name());
      stmt.setString(3, account.email());
      stmt.setString(4, account.password());
      stmt.setBoolean(5, account.googleAccount());
      stmt.setString(6, account.googleId());
      stmt.setString(7, account.pictureUrl());
      stmt.setObject(8, account.lastLoginAt());
      stmt.setString(9, account.authProvider().getValue());
      stmt.setBoolean(10, account.emailVerified());
      stmt.setObject(11, account.createdAt());
      stmt.setObject(12, account.updatedAt());

      stmt.executeUpdate();
      return account;
    } catch (SQLException e) {
      logger.error("Error adding account with email {}: {}", account.email(), e.getMessage(), e);
      throw new RuntimeException("Error adding account: " + e.getMessage(), e);
    }
  }

  @Override
  public Optional<Account> loadByEmail(String email) {
    String sql = "SELECT id, name, email, password, google_account, google_id, picture_url, last_login_at, auth_provider, email_verified, created_at, updated_at " +
        "FROM accounts WHERE email = ?";

    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setString(1, email);

      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          return Optional.of(new Account(
              rs.getString("id"),
              rs.getString("name"),
              rs.getString("email"),
              rs.getString("password"),
              rs.getBoolean("google_account"),
              rs.getString("google_id"),
              rs.getString("picture_url"),
              rs.getTimestamp("last_login_at") != null ? rs.getTimestamp("last_login_at").toLocalDateTime() : null,
              rs.getString("auth_provider"),
              rs.getBoolean("email_verified"),
              rs.getTimestamp("created_at").toLocalDateTime(),
              rs.getTimestamp("updated_at").toLocalDateTime()));
        }
        return Optional.empty();
      }
    } catch (SQLException e) {
      logger.error("Error loading account by email {}: {}", email, e.getMessage(), e);
      throw new RuntimeException("Error loading account by email: " + e.getMessage(), e);
    }
  }

  @Override
  public Optional<Account> loadById(String id) {
    String sql = "SELECT id, name, email, password, google_account, google_id, picture_url, last_login_at, auth_provider, email_verified, created_at, updated_at " +
        "FROM accounts WHERE id = ?";

    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setString(1, id);

      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          return Optional.of(new Account(
              rs.getString("id"),
              rs.getString("name"),
              rs.getString("email"),
              rs.getString("password"),
              rs.getBoolean("google_account"),
              rs.getString("google_id"),
              rs.getString("picture_url"),
              rs.getTimestamp("last_login_at") != null ? rs.getTimestamp("last_login_at").toLocalDateTime() : null,
              rs.getString("auth_provider"),
              rs.getBoolean("email_verified"),
              rs.getTimestamp("created_at").toLocalDateTime(),
              rs.getTimestamp("updated_at").toLocalDateTime()));
        }
        return Optional.empty();
      }
    } catch (SQLException e) {
      logger.error("Error loading account by id {}: {}", id, e.getMessage(), e);
      throw new RuntimeException("Error loading account by id: " + e.getMessage(), e);
    }
  }

  @Override
  public Account update(Account account) {
    String sql = "UPDATE accounts SET name = ?, google_id = ?, picture_url = ?, last_login_at = ?, auth_provider = ?, email_verified = ?, updated_at = ? WHERE id = ?";

    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setString(1, account.name());
      stmt.setString(2, account.googleId());
      stmt.setString(3, account.pictureUrl());
      stmt.setObject(4, account.lastLoginAt());
      stmt.setString(5, account.authProvider().getValue());
      stmt.setBoolean(6, account.emailVerified());
      stmt.setObject(7, account.updatedAt());
      stmt.setString(8, account.id());

      stmt.executeUpdate();
      return account;
    } catch (SQLException e) {
      logger.error("Error updating account {}: {}", account.id(), e.getMessage(), e);
      throw new RuntimeException("Error updating account: " + e.getMessage(), e);
    }
  }
}
