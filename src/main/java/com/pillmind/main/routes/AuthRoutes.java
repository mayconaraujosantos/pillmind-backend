package com.pillmind.main.routes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pillmind.data.protocols.cryptography.Decrypter;
import com.pillmind.domain.usecases.CreateLocalAccount;
import com.pillmind.domain.usecases.LinkOAuthAccount;
import com.pillmind.domain.usecases.LoadUserById;
import com.pillmind.domain.usecases.LocalAuthentication;
import com.pillmind.domain.usecases.UpdateUserProfile;
import com.pillmind.presentation.controllers.GoogleAuthController;
import com.pillmind.presentation.controllers.ProfileController;
import com.pillmind.presentation.controllers.SignInController;
import com.pillmind.presentation.controllers.SignUpController;
import com.pillmind.presentation.controllers.UpdateProfileController;
import com.pillmind.presentation.helpers.LogSanitizer;
import com.pillmind.presentation.protocols.Validation;

import io.javalin.Javalin;

/**
 * Rotas de autenticação e perfil (Sign Up, Sign In, Profile)
 */
public class AuthRoutes implements Routes {
  private static final Logger logger = LoggerFactory.getLogger(AuthRoutes.class);

  private final CreateLocalAccount createLocalAccount;
  private final LocalAuthentication localAuthentication;
  private final Validation<SignUpController.SignUpRequest> signUpValidation;
  private final Validation<SignInController.SignInRequest> signInValidation;
  private final GoogleAuthController googleAuthController;
  private final LoadUserById loadUserById;
  private final UpdateUserProfile updateUserProfile;
  private final Decrypter decrypter;

  // Constructor com injeção de dependências
  public AuthRoutes(
      CreateLocalAccount createLocalAccount,
      LocalAuthentication localAuthentication,
      Validation<SignUpController.SignUpRequest> signUpValidation,
      Validation<SignInController.SignInRequest> signInValidation,
      GoogleAuthController googleAuthController,
      LoadUserById loadUserById,
      UpdateUserProfile updateUserProfile,
      Decrypter decrypter) {
    this.createLocalAccount = createLocalAccount;
    this.localAuthentication = localAuthentication;
    this.signUpValidation = signUpValidation;
    this.signInValidation = signInValidation;
    this.googleAuthController = googleAuthController;
    this.loadUserById = loadUserById;
    this.updateUserProfile = updateUserProfile;
    this.decrypter = decrypter;
  }

  @Override
  public void setup(Javalin app) {
    var signUpController = new SignUpController(createLocalAccount, signUpValidation);
    var signInController = new SignInController(localAuthentication, signInValidation);
    var profileController = new ProfileController(loadUserById, decrypter);
    var updateProfileController = new UpdateProfileController(updateUserProfile, loadUserById, decrypter);

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

    // Route: PUT /api/profile
    app.put("/api/profile", ctx -> {
      logger.info("→ PUT /api/profile - Update user profile");
      updateProfileController.handle(ctx);
      logger.info("✓ Profile updated successfully");
    });
  }
}
