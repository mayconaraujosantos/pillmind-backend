package com.pillmind.presentation.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.pillmind.main.routes.AuthRoutes;
import com.pillmind.presentation.handlers.ErrorHandlers;
import com.pillmind.test.base.IntegrationTestBase;

import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;

/**
 * Testes de integração para SignUpController
 * Usa H2 em memória para performance rápida
 */
@DisplayName("SignUpController Integration Tests")
class SignUpControllerIntegrationTest extends IntegrationTestBase {

    @Test
    @DisplayName("Deve criar usuário com sucesso e persistir no banco H2")
    void shouldCreateUserAndPersistToDatabase() throws Exception {
        JavalinTest.test((app, client) -> {
            setupRoutes(app);

            // Faz requisição de signup
            var response = client.post("/api/signup", """
                    {
                      "name": "John Doe",
                      "email": "john@example.com",
                      "password": "SecurePass123",
                      "dateOfBirth": "1990-05-15",
                      "gender": "MALE"
                    }
                    """);

            // Verifica resposta HTTP
            String responseBody = response.body().string();
            assertEquals(201, response.code());
            assertTrue(responseBody.contains("john@example.com"));
            assertTrue(responseBody.contains("John Doe"));

            // Verifica se foi criado no banco de dados (tabela users)
            int userCount = countRows("email = 'john@example.com'");
            assertEquals(1, userCount, "Usuário deveria ter sido criado no banco de dados");

            // Verifica se conta local foi criada
            int localAccountCount = countLocalAccounts("email = 'john@example.com'");
            assertEquals(1, localAccountCount, "Conta local deveria ter sido criada no banco de dados");
        });
    }

    @Test
    @DisplayName("Não deve criar usuário duplicado com mesmo email")
    void shouldNotCreateDuplicateEmail() throws Exception {
        JavalinTest.test((app, client) -> {
            setupRoutes(app);

            // Primeira requisição - sucesso
            var response1 = client.post("/api/signup", """
                    {
                      "name": "John Doe",
                      "email": "john@example.com",
                      "password": "SecurePass123",
                      "dateOfBirth": "1990-05-15",
                      "gender": "MALE"
                    }
                    """);
            assertEquals(201, response1.code());

            // Segunda requisição com mesmo email - falha
            var response2 = client.post("/api/signup", """
                    {
                      "name": "Jane Doe",
                      "email": "john@example.com",
                      "password": "SecurePass456",
                      "dateOfBirth": "1995-12-25",
                      "gender": "FEMALE"
                    }
                    """);
            assertEquals(409, response2.code());

            // Verifica que apenas um usuário existe no banco
            int userCount = countRows("email = 'john@example.com'");
            assertEquals(1, userCount, "Apenas um usuário com este email deveria existir");
        });
    }

    @Test
    @DisplayName("Deve validar dados obrigatórios")
    void shouldValidateRequiredFields() throws Exception {
        JavalinTest.test((app, client) -> {
            setupRoutes(app);

            var response = client.post("/api/signup", """
                    {
                      "name": "",
                      "email": "invalid",
                      "password": ""
                    }
                    """);

            assertEquals(400, response.code());

            // Verifica que nada foi criado no banco
            int count = countRows(null);
            assertEquals(0, count, "Nenhum usuário deveria ter sido criado");
        });
    }

    @Test
    @DisplayName("Deve hashar a senha do usuário")
    void shouldHashPassword() throws Exception {
        JavalinTest.test((app, client) -> {
            setupRoutes(app);

            var response = client.post("/api/signup", """
                    {
                      "name": "John Doe",
                      "email": "john@example.com",
                      "password": "SecurePass123",
                      "dateOfBirth": "1990-05-15",
                      "gender": "MALE"
                    }
                    """);

            assertEquals(201, response.code());

            // Verifica que a senha foi hasheada (não é igual ao plaintext) na tabela local_accounts
            String hashedPassword = queryOne(
                    "SELECT password_hash FROM local_accounts WHERE email = 'john@example.com'",
                    String.class);

            assertTrue(!hashedPassword.equals("SecurePass123"),
                    "Senha não deveria ser armazenada em plaintext");
            assertTrue(hashedPassword.length() > 20,
                    "Senha hasheada deveria ser longa (BCrypt)");
        });
    }

    @Test
    @DisplayName("Deve definir timestamps created_at e updated_at na criação")
    void shouldSetTimestampsOnCreation() throws Exception {
        JavalinTest.test((app, client) -> {
            setupRoutes(app);

            var response = client.post("/api/signup", """
                    {
                      "name": "John Doe",
                      "email": "john@example.com",
                      "password": "SecurePass123",
                      "dateOfBirth": "1990-05-15",
                      "gender": "MALE"
                    }
                    """);

            assertEquals(201, response.code());

            // Verifica que timestamps foram definidos na tabela users
            var userResult = queryOne(
                    "SELECT created_at FROM users WHERE email = 'john@example.com'",
                    java.sql.Timestamp.class);

            assertTrue(userResult != null, "Timestamps do usuário deveriam estar definidos");

            // Verifica que timestamps foram definidos na tabela local_accounts
            var accountResult = queryOne(
                    "SELECT created_at FROM local_accounts WHERE email = 'john@example.com'",
                    java.sql.Timestamp.class);

            assertTrue(accountResult != null, "Timestamps da conta local deveriam estar definidos");
        });
    }

    /**
     * Helper para setup das rotas
     */
    private void setupRoutes(Javalin app) {
        ErrorHandlers.configure(app);
        var authRoutes = container.resolve("route.auth", AuthRoutes.class);
        try {
            authRoutes.setup(app);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao setup de rotas", e);
        }
    }
}
