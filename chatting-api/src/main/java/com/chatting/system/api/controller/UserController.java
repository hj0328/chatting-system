package com.chatting.system.api.controller;

import com.chatting.system.api.dto.LoginRequest;
import com.chatting.system.api.dto.SignupRequest;
import com.chatting.system.api.dto.UserResponse;
import com.chatting.system.api.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/signup")
    public UserResponse signup(@RequestBody SignupRequest request) {
        log.info("request={}", request);
        UserResponse signup = userService.signup(request.getUsername(), request.getPassword());
        return signup;
    }

    @PostMapping("/login")
    public UserResponse login(@RequestBody LoginRequest request, HttpSession session) {
        UserResponse user = userService.login(request.getUsername(), request.getPassword());
        session.setAttribute("userId", user.getId());
        return user;
    }

    @PostMapping("/logout")
    public void logout(HttpSession session) {
        session.invalidate();
    }
}
