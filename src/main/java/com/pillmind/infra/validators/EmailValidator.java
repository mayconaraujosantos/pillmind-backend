package com.pillmind.infra.validators;

import java.util.regex.Pattern;

/**
 * Validador de email
 */
public class EmailValidator {
  private EmailValidator() {
    // Utility class
  }

  private static final Pattern EMAIL_PATTERN = Pattern.compile(
      "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

  public static boolean isValid(String email) {
    if (email == null || email.isBlank()) {
      return false;
    }
    return EMAIL_PATTERN.matcher(email).matches();
  }
}
