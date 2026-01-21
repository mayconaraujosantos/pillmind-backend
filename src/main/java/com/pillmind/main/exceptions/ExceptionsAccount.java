package com.pillmind.main.exceptions;

public class ExceptionsAccount extends Exception {
    public ExceptionsAccount(String message) {
        super(message);
    }

    public ExceptionsAccount(String message, Throwable cause) {
        super(message, cause);
    }

    public static class AccountAlreadyExistsException extends ExceptionsAccount {
        public AccountAlreadyExistsException(String email) {
            super("Account with email " + email + " already exists");
        }
    }

    public static class AddAccountException extends ExceptionsAccount {
        public AddAccountException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class DatabaseException extends ExceptionsAccount {
        public DatabaseException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
