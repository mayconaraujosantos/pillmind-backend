package com.pillmind.data.protocols.cryptography;

/**
 * Protocolo para encriptação (JWT, etc)
 */
public interface Encrypter {
    String encrypt(String plaintext);
    String decrypt(String ciphertext);
}
