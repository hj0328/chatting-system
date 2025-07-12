package com.chatting.system.api.controller;

import com.chatting.system.api.dto.LoginRequest;
import com.chatting.system.api.dto.SignupRequest;
import com.chatting.system.api.dto.LoginResponse;
import com.chatting.system.api.dto.SignupResponse;
import com.chatting.system.api.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/signup")
    public SignupResponse signup(@RequestBody SignupRequest request) {
        log.info("signup request={}", request);
        SignupResponse signup = userService.signup(request.getUsername(), request.getPassword());
        return signup;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        log.info("login request={}", request);
        LoginResponse response = userService.login(request.getUsername(), request.getPassword());
        log.info("login response={}", response);
        return response;
    }

    @PostMapping("/logout")
    public void logout(HttpSession session) {
        session.invalidate();
    }
}
