package com.pillmind.infra.cryptography;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para BcryptAdapter
 */
public class BcryptAdapterTest {
    @Test
    public void testHashAndCompare() {
        var adapter = new BcryptAdapter(12);
        var plaintext = "password123";
        
        var hashed = adapter.hash(plaintext);
        
        assertNotNull(hashed);
        assertNotEquals(plaintext, hashed);
        assertTrue(adapter.compare(plaintext, hashed));
        assertFalse(adapter.compare("wrong", hashed));
    }
}
