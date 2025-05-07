package com.tunelar.backend.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

/**
 * Provider for JWT token generation and validation.
 */
@Component
public class JwtTokenProvider {

    /** JWT secret key from application properties */
    @Value("${app.jwt-secret}")
    private String jwtSecret;

    /** JWT expiration time in milliseconds from application properties */
    @Value("${app.jwt-expiration-milliseconds}")
    private Long jwtExpirationDate;

    /**
     * Generates a JWT token based on the authentication information.
     *
     * @param authentication the authentication object containing user details
     * @return the generated JWT token
     */
    public String generateToken(final Authentication authentication) {
        final String username = authentication.getName();

        final Date currentDate = new Date();
        final Date expireDate = new Date(currentDate.getTime() + jwtExpirationDate);

        return Jwts.builder()
                .subject(username)
                .issuedAt(currentDate)
                .expiration(expireDate)
                .signWith(key())
                .compact();
    }

    /**
     * Creates a secret key from the JWT secret.
     *
     * @return the secret key for signing and verifying tokens
     */
    private SecretKey key() {
        try {
            // Try original method first
            return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
        }
        catch (final Exception e) {
            try {
                // If that fails, derive a 256-bit key using SHA-256
                final java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
                final byte[] hashedKey = digest.digest(jwtSecret.getBytes(java.nio.charset.StandardCharsets.UTF_8));
                return Keys.hmacShaKeyFor(hashedKey);
            }
            catch (final java.security.NoSuchAlgorithmException nsaEx) {
                throw new RuntimeException("Error creating JWT key", nsaEx);
            }
        }
    }

    /**
     * Extracts the username from a JWT token.
     *
     * @param token the JWT token
     * @return the username contained in the token
     */
    public String getUsername(final String token) {
        final Claims claims = Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    /**
     * Validates a JWT token.
     *
     * @param token the JWT token to validate
     * @return true if the token is valid, false otherwise
     */
    public boolean validateToken(final String token) {
        try {
            Jwts.parser().verifyWith(key()).build().parse(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}