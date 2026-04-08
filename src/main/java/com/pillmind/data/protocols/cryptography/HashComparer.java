package com.pillmind.data.protocols.cryptography;

/**
 * Protocolo para comparação de hash
 */
public interface HashComparer {
    boolean compare(String plaintext, String hashedValue);
}
