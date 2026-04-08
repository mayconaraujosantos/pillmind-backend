package com.pillmind.infra.cryptography;

import com.pillmind.data.protocols.cryptography.Decrypter;
import com.pillmind.data.protocols.cryptography.Encrypter;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

/**
 * Adaptador para JWT
 */
public class JwtAdapter implements Encrypter, Decrypter {
  private final String secret;
  private final long expirationInMs;

  public JwtAdapter(String secret, long expirationInMs) {
    this.secret = secret;
    this.expirationInMs = expirationInMs;
  }

  @Override
  public String encrypt(String plaintext) {
    // Cria a chave HMAC a partir do segredo configurado
    Key key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    long now = System.currentTimeMillis();
    return Jwts.builder()
        .setSubject(plaintext)
        .setIssuedAt(new Date(now))
        .setExpiration(new Date(now + expirationInMs))
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  @Override
  public String decrypt(String ciphertext) {
    Key key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    Jws<Claims> jws = Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(ciphertext);
    return jws.getBody().getSubject();
  }
}
