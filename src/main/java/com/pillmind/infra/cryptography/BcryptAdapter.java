package com.pillmind.infra.cryptography;

import com.pillmind.data.protocols.cryptography.HashComparer;
import com.pillmind.data.protocols.cryptography.Hasher;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Adaptador para BCrypt
 */
public class BcryptAdapter implements Hasher, HashComparer {
    private final int saltRounds;

    public BcryptAdapter(int saltRounds) {
        this.saltRounds = saltRounds;
    }

    @Override
    public String hash(String plaintext) {
        return BCrypt.hashpw(plaintext, BCrypt.gensalt(saltRounds));
    }

    @Override
    public boolean compare(String plaintext, String hashedValue) {
        return BCrypt.checkpw(plaintext, hashedValue);
    }
}
