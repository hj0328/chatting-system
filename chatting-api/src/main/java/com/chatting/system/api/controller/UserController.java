package com.chatting.system.api.controller;

import com.chatting.system.api.dto.LoginRequest;
import com.chatting.system.api.dto.LoginResponse;
import com.chatting.system.api.dto.SignupRequest;
import com.chatting.system.api.dto.SignupResponse;
import com.chatting.system.api.service.UserService;
import com.common.JwtUtil;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/signup")
    public SignupResponse signup(@RequestBody SignupRequest request) {
        log.info("signup request={}", request);
        SignupResponse signup = userService.signup(request.getUsername(), request.getPassword());
        return signup;
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginRequest request) {

        log.info("login request={}", request);
        LoginResponse loginResponse = userService.login(request.getUsername(), request.getPassword());
        String token = jwtUtil.generateToken(loginResponse.getUsername(), loginResponse.getUserId());
        String refreshToken = jwtUtil.createRefreshToken(loginResponse.getUsername(), loginResponse.getUserId());

        userService.addRefreshTokenToRedis(loginResponse.getUserId(), loginResponse.getUsername(), refreshToken);

        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", token)
                .httpOnly(true)
                .secure(true)
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ofMinutes(15)) // 브라우저 기준 토큰 만료 설정
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("Lax")
                .path("/auth/refresh")
                .maxAge(Duration.ofDays(14))
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(loginResponse);
    }

    @PostMapping("/logout")
    public void logout(HttpSession session) {
        session.invalidate();
    }
}
