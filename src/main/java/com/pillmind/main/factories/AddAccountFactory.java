package com.pillmind.main.factories;

import com.pillmind.data.protocols.cryptography.Hasher;
import com.pillmind.data.protocols.db.AddAccountRepository;
import com.pillmind.data.protocols.db.LoadAccountByEmailRepository;
import com.pillmind.data.usecases.DbAddAccount;
import com.pillmind.domain.usecases.AddAccount;
import com.pillmind.infra.cryptography.BcryptAdapter;
import com.pillmind.infra.db.postgres.AccountPostgresRepository;
import com.pillmind.main.config.DatabaseConfig;
import com.pillmind.main.config.Env;

import java.sql.SQLException;

/**
 * Factory para criar instância de AddAccount
 */
public class AddAccountFactory implements Factory<AddAccount> {
  @Override
  public AddAccount make() {
    try {
      var connection = DatabaseConfig.getConnection();
      var addAccountRepository = new AccountPostgresRepository(connection);
      var loadAccountByEmailRepository = new AccountPostgresRepository(connection);
      var hasher = new BcryptAdapter(Env.BCRYPT_SALT_ROUNDS);
      
      return new DbAddAccount(hasher, addAccountRepository, loadAccountByEmailRepository);
    } catch (SQLException e) {
      throw new RuntimeException("Error creating AddAccount", e);
    }
  }
}
