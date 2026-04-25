package com.rexi.pkty.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * FIX #5: JWT Token Utility with Expiration
 * - Tokens now expire after 24 hours
 * - Uses environment variables for secret key
 * - Implements proper token validation
 */
@Component
public class JwtUtil {

    private static final Logger logger = Logger.getLogger(JwtUtil.class.getName());

    @Value("${jwt.secret:RexiVeterinaryClinicManagementSystemSuperSecretKey1234567890!!!}")
    private String secretKey;

    @Value("${jwt.expiration:86400000}")  // Default: 24 hours in milliseconds
    private long expiration;

    private Key getSigningKey() {
        // Ensure secret key is at least 256 bits (32 bytes) for HS256
        byte[] keyBytes = secretKey.getBytes();
        if (keyBytes.length < 32) {
            logger.warning("JWT secret key is less than 32 bytes. This is not secure!");
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Generate JWT token with expiration
     */
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    /**
     * Create token with claims and expiration
     */
    private String createToken(Map<String, Object> claims, String subject) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(expiration, ChronoUnit.MILLIS);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiresAt))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Validate token
     */
    public Boolean validateToken(String token, String username) {
        try {
            final String extractedUsername = extractUsername(token);
            boolean isValid = extractedUsername.equals(username) && !isTokenExpired(token);
            if (!isValid) {
                logger.warning("Token validation failed for user: " + username);
            }
            return isValid;
        } catch (Exception e) {
            logger.warning("Token validation error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Extract username from token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extract expiration date from token
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extract specific claim
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extract all claims from token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Check if token is expired
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}
