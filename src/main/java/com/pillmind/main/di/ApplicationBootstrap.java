package com.pillmind.main.di;

import java.sql.Connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pillmind.data.usecases.DbAddAccount;
import com.pillmind.data.usecases.DbAuthentication;
import com.pillmind.data.usecases.DbLoadAccountById;
import com.pillmind.domain.usecases.AddAccount;
import com.pillmind.domain.usecases.Authentication;
import com.pillmind.domain.usecases.LoadAccountById;
import com.pillmind.infra.cryptography.BcryptAdapter;
import com.pillmind.infra.cryptography.JwtAdapter;
import com.pillmind.infra.db.postgres.AccountPostgresRepository;
import com.pillmind.infra.oauth.GoogleTokenValidator;
import com.pillmind.main.config.DatabaseConfig;
import com.pillmind.main.config.Env;
import com.pillmind.main.routes.AuthRoutes;
import com.pillmind.main.routes.HealthRoutes;
import com.pillmind.main.routes.SwaggerRoutes;
import com.pillmind.presentation.controllers.GoogleAuthController;
import com.pillmind.presentation.validators.SignInValidation;
import com.pillmind.presentation.validators.SignUpValidation;

/**
 * Bootstrap da aplicação
 * Responsável por registrar todas as dependências no container
 * Segue o padrão Composition Root
 */
public class ApplicationBootstrap {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationBootstrap.class);
    private final Container container;

    public ApplicationBootstrap() {
        this.container = new Container();
    }

    /**
     * Inicializa e registra todas as dependências da aplicação
     */
    public void bootstrap() throws Exception {
        logger.info("Iniciando bootstrap da aplicação...");

        // Registra componentes de infraestrutura
        registerInfrastructure(null);

        // Registra casos de uso (use cases)
        registerUseCases();

        // Registra validadores
        registerValidators();

        // Registra rotas
        registerRoutes();

        logger.info("✓ Bootstrap concluído com sucesso!");
    }

    /**
     * Inicializa com conexão customizada (para testes)
     */
    public void bootstrap(Connection customConnection) throws Exception {
        logger.info("Iniciando bootstrap da aplicação com conexão customizada...");

        // Registra componentes de infraestrutura com conexão customizada
        registerInfrastructure(customConnection);

        // Registra casos de uso (use cases)
        registerUseCases();

        // Registra validadores
        registerValidators();

        // Registra rotas
        registerRoutes();

        logger.info("✓ Bootstrap concluído com sucesso!");
    }

    /**
     * Registra componentes de infraestrutura
     */
    private void registerInfrastructure(Connection customConnection) throws Exception {
        logger.debug("Registrando componentes de infraestrutura...");

        // Database Connection (Singleton) - usa customConnection se fornecida
        Connection connection = customConnection != null ? customConnection : DatabaseConfig.getConnection();
        container.registerSingleton("database.connection", connection);

        // Cryptography
        container.registerSingleton("crypto.hasher",
                new BcryptAdapter(Env.BCRYPT_SALT_ROUNDS));

        container.registerSingleton("crypto.jwt",
                new JwtAdapter(Env.JWT_SECRET, Env.JWT_EXPIRATION_IN_MS));

        // Repositories
        container.registerSingleton("repository.account",
                new AccountPostgresRepository(connection));

        // OAuth2
        container.registerSingleton("oauth.google-validator",
                new GoogleTokenValidator(Env.GOOGLE_CLIENT_ID));
    }

    /**
     * Registra casos de uso
     */
    private void registerUseCases() {
        logger.debug("Registrando casos de uso...");

        // AddAccount use case
        container.registerFactory("usecase.add-account", () -> {
            var hasher = container.resolve("crypto.hasher", BcryptAdapter.class);
            var accountRepository = container.resolve("repository.account", AccountPostgresRepository.class);
            return new DbAddAccount(hasher, accountRepository, accountRepository);
        });

        // Authentication use case
        container.registerFactory("usecase.authentication", () -> {
            var accountRepository = container.resolve("repository.account", AccountPostgresRepository.class);
            var hashComparer = container.resolve("crypto.hasher", BcryptAdapter.class);
            var encrypter = container.resolve("crypto.jwt", JwtAdapter.class);
            return new DbAuthentication(accountRepository, hashComparer, encrypter);
        });

        // LoadAccountById use case
        container.registerFactory("usecase.load-account-by-id", () -> {
            var accountRepository = container.resolve("repository.account", AccountPostgresRepository.class);
            return new DbLoadAccountById(accountRepository);
        });
    }

    /**
     * Registra validadores
     */
    private void registerValidators() {
        logger.debug("Registrando validadores...");

        container.registerSingleton("validator.signup", new SignUpValidation());
        container.registerSingleton("validator.signin", new SignInValidation());
    }

    /**
     * Registra rotas
     */
    private void registerRoutes() {
        logger.debug("Registrando rotas...");

        container.registerSingleton("route.swagger", new SwaggerRoutes());
        container.registerSingleton("route.health", new HealthRoutes());

        // AuthRoutes precisa de dependências, então usa factory
        container.registerFactory("route.auth", () -> {
            var addAccount = container.resolve("usecase.add-account", AddAccount.class);
            var authentication = container.resolve("usecase.authentication", Authentication.class);
            var loadAccountById = container.resolve("usecase.load-account-by-id", LoadAccountById.class);
            var signUpValidation = container.resolve("validator.signup", SignUpValidation.class);
            var signInValidation = container.resolve("validator.signin", SignInValidation.class);
            var googleTokenValidator = container.resolve("oauth.google-validator", GoogleTokenValidator.class);
            var decrypter = container.resolve("crypto.jwt", JwtAdapter.class);
            var googleAuthController = new GoogleAuthController(addAccount, authentication, googleTokenValidator);

            return new AuthRoutes(
                addAccount,
                authentication,
                signUpValidation,
                signInValidation,
                googleAuthController,
                loadAccountById,
                decrypter);
        });
    }

    /**
     * Retorna o container configurado
     */
    public Container getContainer() {
        return container;
    }
}
