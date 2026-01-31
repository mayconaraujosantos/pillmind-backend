package com.pillmind.main.factories;

import java.sql.SQLException;

import com.pillmind.data.usecases.DbAuthentication;
import com.pillmind.domain.usecases.Authentication;
import com.pillmind.infra.cryptography.BcryptAdapter;
import com.pillmind.infra.cryptography.JwtAdapter;
import com.pillmind.infra.db.postgres.AccountPostgresRepository;
import com.pillmind.main.config.DatabaseConfig;
import com.pillmind.main.config.Env;

/**
 * Factory para criar instância de Authentication
 */
public class AuthenticationFactory implements Factory<Authentication> {
  @Override
  public Authentication make() {
    try {
      var connection = DatabaseConfig.getConnection();
      var loadAccountByEmailRepository = new AccountPostgresRepository(connection);
      var hashComparer = new BcryptAdapter(Env.BCRYPT_SALT_ROUNDS);
      var encrypter = new JwtAdapter(Env.JWT_SECRET, Env.JWT_EXPIRATION_IN_MS);

      return new DbAuthentication(loadAccountByEmailRepository, hashComparer, encrypter);
    } catch (SQLException e) {
      throw new RuntimeException("Error creating Authentication", e);
    }
  }
}
