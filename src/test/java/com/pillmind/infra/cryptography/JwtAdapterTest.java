package com.pillmind.infra.cryptography;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/**
 * Testes para JwtAdapter
 */
class JwtAdapterTest {
    @Test
    void testEncryptAndDecrypt() {
        var secret = "test-secret-key-min-256-bits-for-hmac-sha-256-algorithm";
        var adapter = new JwtAdapter(secret, 3600000);
        var subject = "user123";
        
        var token = adapter.encrypt(subject);
        
        assertNotNull(token);
        assertEquals(subject, adapter.decrypt(token));
    }
}
