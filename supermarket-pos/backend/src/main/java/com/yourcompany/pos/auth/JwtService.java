package com.yourcompany.pos.auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    private final byte[] secret;
    private final long expirationMinutes;

    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.expirationMinutes}") long expirationMinutes) {
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
        this.expirationMinutes = expirationMinutes;
    }

    public String generateToken(String username) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(expirationMinutes * 60);
        return Jwts.builder()
            .subject(username)
            .issuedAt(Date.from(now))
            .expiration(Date.from(exp))
            .signWith(Keys.hmacShaKeyFor(secret))
            .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parser()
            .verifyWith(Keys.hmacShaKeyFor(secret))
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getSubject();
    }
}
