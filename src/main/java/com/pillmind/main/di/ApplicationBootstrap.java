package com.pillmind.main.di;

import java.sql.Connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pillmind.data.usecases.DbCreateLocalAccount;
import com.pillmind.data.usecases.DbLinkOAuthAccount;
import com.pillmind.data.usecases.DbLoadUserById;
import com.pillmind.data.usecases.DbLocalAuthentication;
import com.pillmind.data.usecases.DbUpdateUserProfile;
import com.pillmind.domain.usecases.CreateLocalAccount;
import com.pillmind.domain.usecases.LinkOAuthAccount;
import com.pillmind.domain.usecases.LoadUserById;
import com.pillmind.domain.usecases.LocalAuthentication;
import com.pillmind.domain.usecases.UpdateUserProfile;
import com.pillmind.infra.cryptography.BcryptAdapter;
import com.pillmind.infra.cryptography.JwtAdapter;
import com.pillmind.infra.db.postgres.LocalAccountPostgresRepository;
import com.pillmind.infra.db.postgres.OAuthAccountPostgresRepository;
import com.pillmind.infra.db.postgres.UserPostgresRepository;
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

        // Repositories - Nova estrutura
        container.registerSingleton("repository.user",
                new UserPostgresRepository(connection));
        
        container.registerSingleton("repository.local-account", 
                new LocalAccountPostgresRepository(connection));
        
        container.registerSingleton("repository.oauth-account", 
                new OAuthAccountPostgresRepository(connection));

        // OAuth2
        container.registerSingleton("oauth.google-validator",
                new GoogleTokenValidator(Env.GOOGLE_CLIENT_ID));
    }

    /**
     * Registra casos de uso - Nova estrutura
     */
    private void registerUseCases() {
        logger.debug("Registrando casos de uso...");

        // CreateLocalAccount use case
        container.registerFactory("usecase.create-local-account", () -> {
            var hasher = container.resolve("crypto.hasher", BcryptAdapter.class);
            var userRepository = container.resolve("repository.user", UserPostgresRepository.class);
            var localAccountRepository = container.resolve("repository.local-account", LocalAccountPostgresRepository.class);
            return new DbCreateLocalAccount(hasher, userRepository, localAccountRepository);
        });

        // LocalAuthentication use case
        container.registerFactory("usecase.local-authentication", () -> {
            var localAccountRepository = container.resolve("repository.local-account", LocalAccountPostgresRepository.class);
            var userRepository = container.resolve("repository.user", UserPostgresRepository.class);
            var hashComparer = container.resolve("crypto.hasher", BcryptAdapter.class);
            var encrypter = container.resolve("crypto.jwt", JwtAdapter.class);
            return new DbLocalAuthentication(localAccountRepository, userRepository, hashComparer, encrypter);
        });

        // LoadUserById use case
        container.registerFactory("usecase.load-user-by-id", () -> {
            var userRepository = container.resolve("repository.user", UserPostgresRepository.class);
            return new DbLoadUserById(userRepository);
        });

        // UpdateUserProfile use case
        container.registerFactory("usecase.update-user-profile", () -> {
            var userRepository = container.resolve("repository.user", UserPostgresRepository.class);
            return new DbUpdateUserProfile(userRepository);
        });

        // LinkOAuthAccount use case
        container.registerFactory("usecase.link-oauth-account", () -> {
            var userRepository = container.resolve("repository.user", UserPostgresRepository.class);
            var oauthAccountRepository = container.resolve("repository.oauth-account", OAuthAccountPostgresRepository.class);
            return new DbLinkOAuthAccount(userRepository, oauthAccountRepository);
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
     * Registra rotas - Nova estrutura
     */
    private void registerRoutes() {
        logger.debug("Registrando rotas...");

        container.registerSingleton("route.health", new HealthRoutes());
        container.registerSingleton("route.swagger", new SwaggerRoutes());

        // AuthRoutes precisa de dependências, então usa factory
        container.registerFactory("route.auth", () -> {
            var createLocalAccount = container.resolve("usecase.create-local-account", CreateLocalAccount.class);
            var localAuthentication = container.resolve("usecase.local-authentication", LocalAuthentication.class);
            var loadUserById = container.resolve("usecase.load-user-by-id", LoadUserById.class);
            var updateUserProfile = container.resolve("usecase.update-user-profile", UpdateUserProfile.class);
            var linkOAuthAccount = container.resolve("usecase.link-oauth-account", LinkOAuthAccount.class);
            var signUpValidation = container.resolve("validator.signup", SignUpValidation.class);
            var signInValidation = container.resolve("validator.signin", SignInValidation.class);
            var googleTokenValidator = container.resolve("oauth.google-validator", GoogleTokenValidator.class);
            var decrypter = container.resolve("crypto.jwt", JwtAdapter.class);
            var encrypter = container.resolve("crypto.jwt", JwtAdapter.class);
            var googleAuthController = new GoogleAuthController(linkOAuthAccount, encrypter, googleTokenValidator);

            return new AuthRoutes(
                    createLocalAccount,
                    localAuthentication,
                    signUpValidation,
                    signInValidation,
                    googleAuthController,
                    loadUserById,
                    updateUserProfile,
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
