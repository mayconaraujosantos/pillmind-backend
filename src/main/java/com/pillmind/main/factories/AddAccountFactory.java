package com.pillmind.main.factories;

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
      var jdbi = DatabaseConfig.getJdbi();
      var addAccountRepository = new AccountPostgresRepository(jdbi);
      var loadAccountByEmailRepository = new AccountPostgresRepository(jdbi);
      var hasher = new BcryptAdapter(Env.BCRYPT_SALT_ROUNDS);

      return new DbAddAccount(hasher, addAccountRepository, loadAccountByEmailRepository);
    } catch (Exception e) {
      logger.error("Unexpected error creating AddAccount factory: {}", e.getMessage(), e);
      throw new ExceptionsAccount.DatabaseException("Unexpected error creating AddAccount: " + e.getMessage(), e);
    }
  }
}
