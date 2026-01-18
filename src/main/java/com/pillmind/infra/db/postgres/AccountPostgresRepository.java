package com.pillmind.infra.db.postgres;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import com.pillmind.data.protocols.db.AddAccountRepository;
import com.pillmind.data.protocols.db.LoadAccountByEmailRepository;
import com.pillmind.domain.models.Account;

/**
 * Implementação do repositório de Account usando PostgreSQL
 */
public class AccountPostgresRepository extends PostgresRepository
    implements AddAccountRepository, LoadAccountByEmailRepository {

  public AccountPostgresRepository(Connection connection) {
    super(connection);
  }

  @Override
  public Account add(Account account) {
    String sql = "INSERT INTO accounts (id, name, email, password, google_account) VALUES (?, ?, ?, ?, ?)";

    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setString(1, account.id());
      stmt.setString(2, account.name());
      stmt.setString(3, account.email());
      stmt.setString(4, account.password());
      stmt.setBoolean(5, account.googleAccount());

      stmt.executeUpdate();
      return account;
    } catch (SQLException e) {
      System.err.println("SQL Error adding account: " + e.getMessage());
      e.printStackTrace();
      throw new RuntimeException("Error adding account", e);
    }
  }

  @Override
  public Optional<Account> loadByEmail(String email) {
    String sql = "SELECT id, name, email, password, google_account FROM accounts WHERE email = ?";

    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setString(1, email);

      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          return Optional.of(new Account(
              rs.getString("id"),
              rs.getString("name"),
              rs.getString("email"),
              rs.getString("password"),
              rs.getBoolean("google_account")));
        }
        return Optional.empty();
      }
    } catch (SQLException e) {
      throw new RuntimeException("Error loading account by email", e);
    }
  }
}
