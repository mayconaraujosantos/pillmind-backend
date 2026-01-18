package com.pillmind.infra.cryptography;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para JwtAdapter
 */
public class JwtAdapterTest {
    @Test
    public void testEncryptAndDecrypt() {
        var secret = "test-secret-key-min-256-bits-for-hmac-sha-256-algorithm";
        var adapter = new JwtAdapter(secret, 3600000);
        var subject = "user123";
        
        var token = adapter.encrypt(subject);
        
        assertNotNull(token);
        assertEquals(subject, adapter.decrypt(token));
    }
}
