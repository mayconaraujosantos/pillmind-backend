package com.pillmind.data.protocols.cryptography;

/**
 * Protocolo para hash de senhas
 */
public interface Hasher {
    String hash(String plaintext);
}
