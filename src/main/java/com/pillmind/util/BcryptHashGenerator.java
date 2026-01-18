package com.pillmind.util;

import com.pillmind.infra.cryptography.BcryptAdapter;

/**
 * Utilitário para gerar hashes BCrypt
 * Uso: java -cp ... com.pillmind.util.BcryptHashGenerator "senha"
 */
public class BcryptHashGenerator {
  public static void main(String[] args) {
    if (args.length == 0) {
      System.out.println("Uso: BcryptHashGenerator <senha>");
      System.out.println("Exemplo: BcryptHashGenerator password123");
      System.exit(1);
    }

    String password = args[0];
    BcryptAdapter adapter = new BcryptAdapter(12);
    String hash = adapter.hash(password);

    System.out.println("==========================================");
    System.out.println("Senha: " + password);
    System.out.println("Hash:  " + hash);
    System.out.println("==========================================");
    System.out.println();
    System.out.println("Para usar no seed.sql, substitua:");
    System.out.println("  '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyY5Y5Y5Y5Y5'");
    System.out.println("por:");
    System.out.println("  '" + hash + "'");
  }
}
