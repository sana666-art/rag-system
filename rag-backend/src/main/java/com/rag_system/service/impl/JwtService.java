package com.rag_system.service.impl;

import com.rag_system.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.accessExpiration}")
    private long accessExpiration;

    @Value("${jwt.refreshExpiration}")
    private long refreshExpiration;

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateAccessToken(User user) {
        return generateToken(new HashMap<>(), user, accessExpiration);
    }

    public String generateRefreshToken(User user) {
        return generateToken(new HashMap<>(), user, refreshExpiration);
    }

    public String generateToken(Map<String, Object> extraClaims, User user, long expiration) {
        return buildToken(extraClaims, user, expiration);
    }

    public boolean isTokenValid(String token, User user) {
        final String email = extractEmail(token);
        final Integer tokenVersion = extractClaim(token, claims -> claims.get("tokenVersion", Integer.class));
        return email.equals(user.getEmail())
                && isTokenExpired(token)
                && tokenVersion != null
                && tokenVersion.equals(user.getTokenVersion());
    }

    public boolean isTokenValid(String token) {
        return isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return !extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            User user,
            long expiration
    ) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expiration);

        extraClaims.put("id", user.getId());
        extraClaims.put("role", user.getRole().name());
        extraClaims.put("tokenVersion", user.getTokenVersion());
        extraClaims.put("nonce", UUID.randomUUID().toString());

        return Jwts.builder()
                .claims(extraClaims)
                .subject(user.getEmail())
                .issuedAt(now)
                .expiration(expiry)
                .signWith(getSignInKey())
                .compact();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Key getSignInKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
