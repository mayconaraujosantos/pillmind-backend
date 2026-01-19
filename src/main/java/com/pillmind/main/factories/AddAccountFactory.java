package com.pillmind.main.factories;

import java.sql.SQLException;

import com.pillmind.data.usecases.DbAddAccount;
import com.pillmind.domain.usecases.AddAccount;
import com.pillmind.infra.cryptography.BcryptAdapter;
import com.pillmind.infra.db.postgres.AccountPostgresRepository;
import com.pillmind.main.config.DatabaseConfig;
import com.pillmind.main.config.Env;
import com.pillmind.main.exceptions.ExceptionsAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory para criar instância de AddAccount
 */
public class AddAccountFactory implements Factory<AddAccount> {
  private static final Logger logger = LoggerFactory.getLogger(AddAccountFactory.class);

  @Override
  public AddAccount make() throws Exception {
    try {
      var connection = DatabaseConfig.getConnection();

      if (connection == null || connection.isClosed()) {
        logger.error("Database connection is null or closed");
        throw new ExceptionsAccount.DatabaseException("Database connection is not available", null);
      }
      var addAccountRepository = new AccountPostgresRepository(connection);
      var loadAccountByEmailRepository = new AccountPostgresRepository(connection);
      var hasher = new BcryptAdapter(Env.BCRYPT_SALT_ROUNDS);

      return new DbAddAccount(hasher, addAccountRepository, loadAccountByEmailRepository);
    } catch (SQLException e) {
      logger.error("SQL Error creating AddAccount factory: {}", e.getMessage(), e);
      throw new ExceptionsAccount.DatabaseException("Error creating AddAccount: " + e.getMessage(), e);
    } catch (Exception e) {
      logger.error("Unexpected error creating AddAccount factory: {}", e.getMessage(), e);
      throw new ExceptionsAccount.DatabaseException("Unexpected error creating AddAccount: " + e.getMessage(), e);
    }
  }
}
