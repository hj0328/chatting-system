package com.chatting.system.api.controller;

import com.chatting.system.api.dto.RefreshRequest;
import com.common.JwtUserInfo;
import com.common.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final RedisTemplate<String, String> redisTemplate;
    private final JwtUtil jwtUtil;

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshRequest userRequest,
                                          HttpServletRequest request) {
        log.info("GET /auth/refresh, userId={}", userRequest);
        String refreshToken = extractCookie(request, "refreshToken");

        if (refreshToken == null || !jwtUtil.isTokenValid(refreshToken, false)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("리프레시 토큰 만료 또는 없음");
        }

        JwtUserInfo userInfo = jwtUtil.getJwtUserInfo(refreshToken, false);

        String savedToken = redisTemplate.opsForValue().get("refresh:" + userInfo.getUserId() + ":"+ userInfo.getUsername());

        if (!refreshToken.equals(savedToken) || !userInfo.getUserId().equals(userRequest.userId())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("위조된 리프레시 토큰");
        }

        String newAccessToken = jwtUtil.generateToken(userInfo.getUsername(), userInfo.getUserId());
        ResponseCookie newAccessCookie = ResponseCookie.from("accessToken", newAccessToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(Duration.ofMinutes(15))
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, newAccessCookie.toString())
                .body(Map.of("message", "accessToken 재발급 완료"));
    }

    private String extractCookie(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;
        for (Cookie c : request.getCookies()) {
            if (c.getName().equals(name)) {
                return c.getValue();
            }
        }
        return null;
    }

}
