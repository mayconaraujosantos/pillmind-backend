package com.pillmind.data.protocols.cryptography;

/**
 * Protocolo para descriptografia
 */
public interface Decrypter {
  String decrypt(String ciphertext);
}
