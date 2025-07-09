package com.chatting.system.api.controller;

import com.chatting.system.api.dto.LoginRequest;
import com.chatting.system.api.dto.SignupRequest;
import com.chatting.system.api.dto.UserResponse;
import com.chatting.system.api.service.UserService;
import com.chatting.system.api.util.JwtUtil;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/signup")
    public UserResponse signup(@RequestBody SignupRequest request) {
        log.info("request={}", request);
        UserResponse signup = userService.signup(request.getUsername(), request.getPassword());
        return signup;
    }

    @PostMapping("/login")
    public UserResponse login(@RequestBody LoginRequest request) {
        UserResponse user = userService.login(request.getUsername(), request.getPassword());
        return user;
    }

    @PostMapping("/logout")
    public void logout(HttpSession session) {
        session.invalidate();
    }
}
