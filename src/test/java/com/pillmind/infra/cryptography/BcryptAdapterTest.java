package com.pillmind.infra.cryptography;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Testes para BcryptAdapter
 */
class BcryptAdapterTest {
    @Test
    void testHashAndCompare() {
        var adapter = new BcryptAdapter(12);
        var plaintext = "password123";
        
        var hashed = adapter.hash(plaintext);
        
        assertNotNull(hashed);
        assertNotEquals(plaintext, hashed);
        assertTrue(adapter.compare(plaintext, hashed));
        assertFalse(adapter.compare("wrong", hashed));
    }
}
