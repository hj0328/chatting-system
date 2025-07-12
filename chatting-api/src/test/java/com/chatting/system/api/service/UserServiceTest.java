package com.chatting.system.api.service;


import com.chatting.system.api.dto.LoginResponse;
import com.chatting.system.api.dto.SignupResponse;
import com.chatting.system.api.entity.User;
import com.chatting.system.api.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.common.JwtUtil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    BCryptPasswordEncoder passwordEncoder;

    @Mock
    JwtUtil jwtUtil;

    @InjectMocks
    UserService userService;

    @Test
    void signupSuccess() {
        when(userRepository.findByUsername("testuser")).thenReturn(null);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class)))
                .thenReturn(User.sign("testuser", "encodedPassword"));

        SignupResponse response = userService.signup("testuser", "password");

        assertThat(response.getUsername()).isEqualTo("testuser");
    }

    @Test
    void signupFailDuplicateUser() {
        when(userRepository.findByUsername("testuser")).thenReturn(new User());

        assertThatThrownBy(() -> userService.signup("testuser", "password"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 존재하는 사용자입니다.");
    }

    @Test
    void loginSuccess() {
        User user = User.sign("testuser", "encodedPassword");

        when(jwtUtil.generateToken(any(), any())).thenReturn("token");
        when(userRepository.findByUsername("testuser")).thenReturn(user);
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);

        LoginResponse response = userService.login("testuser", "password");

        assertThat(response.getUsername()).isEqualTo("testuser");
    }

    @Test
    void loginFailInvalidPassword() {
        User user = User.sign("testuser", "encodedPassword");

        when(userRepository.findByUsername("testuser")).thenReturn(user);
        when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);

        assertThatThrownBy(() -> userService.login("testuser", "wrongpassword"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("아이디 또는 비밀번호가 올바르지 않습니다.");
    }

    @Test
    void loginFailUserNotFound() {
        when(userRepository.findByUsername("testuser")).thenReturn(null);

        assertThatThrownBy(() -> userService.login("testuser", "password"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("아이디 또는 비밀번호가 올바르지 않습니다.");
    }

}