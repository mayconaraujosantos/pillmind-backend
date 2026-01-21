package com.pillmind.main.routes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pillmind.domain.usecases.AddAccount;
import com.pillmind.domain.usecases.Authentication;
import com.pillmind.presentation.controllers.GoogleAuthController;
import com.pillmind.presentation.controllers.SignInController;
import com.pillmind.presentation.controllers.SignUpController;
import com.pillmind.presentation.helpers.LogSanitizer;
import com.pillmind.presentation.protocols.Validation;

import io.javalin.Javalin;

/**
 * Rotas de autenticação (Sign Up e Sign In)
 */
public class AuthRoutes implements Routes {
  private static final Logger logger = LoggerFactory.getLogger(AuthRoutes.class);

  private final AddAccount addAccount;
  private final Authentication authentication;
  private final Validation<SignUpController.SignUpRequest> signUpValidation;
  private final Validation<SignInController.SignInRequest> signInValidation;
  private final GoogleAuthController googleAuthController;

  // Constructor com injeção de dependências
  public AuthRoutes(
      AddAccount addAccount,
      Authentication authentication,
      Validation<SignUpController.SignUpRequest> signUpValidation,
      Validation<SignInController.SignInRequest> signInValidation,
      GoogleAuthController googleAuthController) {
    this.addAccount = addAccount;
    this.authentication = authentication;
    this.signUpValidation = signUpValidation;
    this.signInValidation = signInValidation;
    this.googleAuthController = googleAuthController;
  }

  @Override
  public void setup(Javalin app) throws Exception {
    try {
      var signUpController = new SignUpController(addAccount, authentication, signUpValidation);
      var signInController = new SignInController(authentication, signInValidation);

      // Route: POST /api/signup
      app.post("/api/signup", ctx -> {
        try {
          logger.info("→ POST /api/signup - Body: {}", LogSanitizer.sanitizeRequestBody(ctx.body()));
          signUpController.handle(ctx);
          logger.info("✓ Signup concluído com sucesso");
        } catch (Exception e) {
          logger.error("✗ Erro em /api/signup", e);
          throw e;
        }
      });

      // Route: POST /api/signin
      app.post("/api/signin", ctx -> {
        try {
          logger.info("→ POST /api/signin - Body: {}", LogSanitizer.sanitizeRequestBody(ctx.body()));
          signInController.handle(ctx);
          logger.info("✓ Signin concluído com sucesso");
        } catch (Exception e) {
          logger.error("✗ Erro em /api/signin", e);
          throw e;
        }
      });

      // Route: POST /api/auth/google
      app.post("/api/auth/google", ctx -> {
        try {
          logger.info("→ POST /api/auth/google - Google OAuth2");
          googleAuthController.handle(ctx);
          logger.info("✓ Google auth concluído com sucesso");
        } catch (Exception e) {
          logger.error("✗ Erro em /api/auth/google", e);
          throw e;
        }
      });
    } catch (Exception e) {
      logger.error("✗ Erro ao configurar AuthRoutes", e);
      throw e;
    }
  }
}
