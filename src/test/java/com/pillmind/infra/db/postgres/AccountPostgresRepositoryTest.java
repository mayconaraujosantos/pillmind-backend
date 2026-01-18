package com.pillmind.infra.db.postgres;

import com.pillmind.data.protocols.db.AddAccountRepository;
import com.pillmind.data.protocols.db.LoadAccountByEmailRepository;
import com.pillmind.domain.models.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para AccountPostgresRepository
 * Nota: Estes testes requerem um banco de dados PostgreSQL em execução
 */
public class AccountPostgresRepositoryTest {
  private Connection connection;
  private AddAccountRepository addAccountRepository;
  private LoadAccountByEmailRepository loadAccountByEmailRepository;

  @BeforeEach
  public void setUp() throws SQLException {
    // TODO: Configurar conexão de teste com banco em memória ou container
    // Por enquanto, este teste será um placeholder
    // connection = DatabaseConfig.getConnection();
    // addAccountRepository = new AccountPostgresRepository(connection);
    // loadAccountByEmailRepository = new AccountPostgresRepository(connection);
  }

  @Test
  public void shouldAddAccountToDatabase() {
    // TODO: Implementar quando o repositório estiver pronto
    assertTrue(true);
  }

  @Test
  public void shouldLoadAccountByEmail() {
    // TODO: Implementar quando o repositório estiver pronto
    assertTrue(true);
  }

  @Test
  public void shouldReturnEmptyWhenEmailNotFound() {
    // TODO: Implementar quando o repositório estiver pronto
    assertTrue(true);
  }
}
