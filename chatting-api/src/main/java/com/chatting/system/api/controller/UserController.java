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
        String accessToken = jwtUtil.generateToken(loginResponse.getUsername(), loginResponse.getUserId());
        loginResponse.addAccessToken(accessToken);

        String refreshToken = jwtUtil.createRefreshToken(loginResponse.getUsername(), loginResponse.getUserId());

        userService.addRefreshTokenToRedis(loginResponse.getUserId(), loginResponse.getUsername(), refreshToken);

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(Duration.ofDays(14))
                .build();

        log.info("login response: {}", loginResponse);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(loginResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        ResponseCookie expiredAccess = ResponseCookie.from("accessToken", "")
                .path("/")
                .maxAge(0)
                .secure(true)
                .httpOnly(true)
                .sameSite("Strict")
                .build();

        ResponseCookie expiredRefresh = ResponseCookie.from("refreshToken", "")
                .path("/")
                .maxAge(0)
                .secure(true)
                .httpOnly(true)
                .sameSite("Strict")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, expiredAccess.toString())
                .header(HttpHeaders.SET_COOKIE, expiredRefresh.toString())
                .body("로그아웃 완료");
    }
}
