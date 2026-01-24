package com.pillmind.main.routes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pillmind.domain.usecases.AddAccount;
import com.pillmind.domain.usecases.Authentication;
import com.pillmind.presentation.controllers.GoogleAuthController;
import com.pillmind.presentation.controllers.ProfileController;
import com.pillmind.presentation.controllers.SignInController;
import com.pillmind.presentation.controllers.SignUpController;
import com.pillmind.data.protocols.cryptography.Decrypter;
import com.pillmind.domain.usecases.LoadAccountById;
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
  private final LoadAccountById loadAccountById;
  private final Decrypter decrypter;

  // Constructor com injeção de dependências
  public AuthRoutes(
      AddAccount addAccount,
      Authentication authentication,
      Validation<SignUpController.SignUpRequest> signUpValidation,
      Validation<SignInController.SignInRequest> signInValidation,
      GoogleAuthController googleAuthController,
      LoadAccountById loadAccountById,
      Decrypter decrypter) {
    this.addAccount = addAccount;
    this.authentication = authentication;
    this.signUpValidation = signUpValidation;
    this.signInValidation = signInValidation;
    this.googleAuthController = googleAuthController;
    this.loadAccountById = loadAccountById;
    this.decrypter = decrypter;
  }

  @Override
  public void setup(Javalin app) throws Exception {
    var signUpController = new SignUpController(addAccount, authentication, signUpValidation);
    var signInController = new SignInController(authentication, signInValidation);
    var profileController = new ProfileController(loadAccountById, decrypter);

    // Route: POST /api/signup
    app.post("/api/signup", ctx -> {
      logger.info("→ POST /api/signup - Body: {}", LogSanitizer.sanitizeRequestBody(ctx.body()));
      signUpController.handle(ctx);
      logger.info("✓ Signup concluído com sucesso");
    });

    // Route: POST /api/signin
    app.post("/api/signin", ctx -> {
      logger.info("→ POST /api/signin - Body: {}", LogSanitizer.sanitizeRequestBody(ctx.body()));
      signInController.handle(ctx);
      logger.info("✓ Signin concluído com sucesso");
    });

    // Route: POST /api/auth/google
    app.post("/api/auth/google", ctx -> {
      logger.info("→ POST /api/auth/google - Google OAuth2");
      googleAuthController.handle(ctx);
      logger.info("✓ Google auth concluído com sucesso");
    });

    // Route: GET /api/profile
    app.get("/api/profile", ctx -> {
      logger.info("→ GET /api/profile - User profile");
      profileController.handle(ctx);
    });
  }
}
