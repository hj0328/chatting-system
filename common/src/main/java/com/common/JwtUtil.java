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
    private String SECRET_KEY;

    @Value("${jwt.expiration}")
    private long TOKEN_EXP;

    @Value("${jwt.refresh.expiration}")
    private long REFRESH_EXP;

    public String generateToken(String username, Long userId) {
        return Jwts.builder()
                .claim("username", username) // 커스텀 필드 username 추가
                .claim("userId", userId)     // 커스텀 필드 userId 추가
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_EXP)) // 서버 기준 토큰 만료
                .signWith(getSigningKey(),SignatureAlgorithm.HS256)
                .compact();
    }

    public JwtUserInfo getJwtUserInfo(String token) {
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
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String createRefreshToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_EXP))
                .signWith(getSigningKey(),SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parser().setSigningKey(getSigningKey()).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
