package com.pillmind.main.routes;

import com.pillmind.domain.usecases.AddAccount;
import com.pillmind.domain.usecases.Authentication;
import com.pillmind.main.factories.AddAccountFactory;
import com.pillmind.main.factories.AuthenticationFactory;
import com.pillmind.presentation.controllers.SignInController;
import com.pillmind.presentation.controllers.SignUpController;
import com.pillmind.presentation.protocols.Validation;
import com.pillmind.presentation.validators.SignInValidation;
import com.pillmind.presentation.validators.SignUpValidation;
import io.javalin.Javalin;

/**
 * Rotas de autenticação (Sign Up e Sign In)
 */
public class AuthRoutes implements Routes {
  @Override
  public void setup(Javalin app) {
    var addAccount = new AddAccountFactory().make();
    var authentication = new AuthenticationFactory().make();
    var signUpValidation = new SignUpValidation();
    var signInValidation = new SignInValidation();

    var signUpController = new SignUpController(addAccount, signUpValidation);
    var signInController = new SignInController(authentication, signInValidation);

    app.post("/api/signup", signUpController::handle);
    app.post("/api/signin", signInController::handle);
  }
}
