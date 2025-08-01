package com.chatting.system.api.controller;

import com.chatting.system.api.config.SecurityConfig;
import com.chatting.system.api.dto.LoginResponse;
import com.chatting.system.api.dto.SignupRequest;
import com.chatting.system.api.dto.SignupResponse;
import com.chatting.system.api.service.UserService;
import com.common.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import({SecurityConfig.class})
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @MockBean
    RedisTemplate<String, String> redisTemplate;

    @MockBean
    RedisConnectionFactory redisConnectionFactory;

    @MockBean
    JwtUtil jwtUtil;

    @Test
    @DisplayName("회원가입 성공")
    void signupSuccess() throws Exception {
        // given
        SignupRequest request = new SignupRequest("testuser", "password");

        // when
        Mockito.when(userService.signup("testuser", "password"))
                .thenReturn(new SignupResponse(1L, "testuser"));

        // then
        mockMvc.perform(post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testuser\",\"password\":\"password\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    @DisplayName("회원가입 실패 - 이미 존재하는 사용자")
    void signupFailDuplicateUser() throws Exception {
        Mockito.when(userService.signup(anyString(), anyString()))
                .thenThrow(new IllegalArgumentException("이미 존재하는 사용자입니다."));

        mockMvc.perform(post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testuser\",\"password\":\"password\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("이미 존재하는 사용자입니다."));
    }

    @Test
    @DisplayName("로그인 성공")
    void loginSuccess() throws Exception {
//        LoginRequest request = new LoginRequest("testuser", "password");

        // login stub
        Mockito.when(userService.login("testuser", "password"))
                .thenReturn(new LoginResponse(1L, "testuser"));

        // jwt stub
        Mockito.when(jwtUtil.generateToken("testuser", 1L))
                .thenReturn("access-token");

        Mockito.when(jwtUtil.createRefreshToken("testuser"))
                .thenReturn("refresh-token");

        Mockito.doNothing().when(userService)
                .addRefreshTokenToRedis(1L, eq("testuser"), eq("refresh-token"));

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testuser\",\"password\":\"password\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    @DisplayName("로그인 실패 - 잘못된 비밀번호")
    void loginFailInvalidPassword() throws Exception {
        Mockito.when(userService.login(anyString(), anyString()))
                .thenThrow(new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다."));

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testuser\",\"password\":\"wrongpassword\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("아이디 또는 비밀번호가 올바르지 않습니다."));
    }
}