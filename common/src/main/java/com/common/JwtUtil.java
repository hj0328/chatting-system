package com.common;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationTime;

    public String generateToken(String username, Long userId) {
        return Jwts.builder()
                .claim("username", username) // 커스텀 필드 username 추가
                .claim("userId", userId)     // 커스텀 필드 userId 추가
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSigningKey(),SignatureAlgorithm.HS256)
                .compact();
    }

    public JwtUserInfo validateTokenAndGetJwtUserInfo(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        String username = claims.get("username", String.class);
        Long userId = claims.get("userId", Long.class);
        return JwtUserInfo.builder()
                .userId(userId)
                .username(username)
                .build();
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
}
