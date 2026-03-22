package com.pillmind.domain.errors;

/**
 * Recurso temporariamente indisponível (ex.: armazenamento de objetos desligado).
 */
public class ServiceUnavailableException extends RuntimeException {
  public ServiceUnavailableException(String message) {
    super(message);
  }

  public ServiceUnavailableException(String message, Throwable cause) {
    super(message, cause);
  }
}
