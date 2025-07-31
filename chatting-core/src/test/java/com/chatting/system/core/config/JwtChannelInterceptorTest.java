package com.chatting.system.core.config;

import com.common.JwtUserInfo;
import com.common.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtChannelInterceptorTest {

    private JwtUtil jwtUtil;
    private StompAuthChannelInterceptor interceptor;

    @BeforeEach
    void setUp() {
        jwtUtil = mock(JwtUtil.class);
        interceptor = new StompAuthChannelInterceptor(jwtUtil);
        SecurityContextHolder.clearContext(); // 초기화
    }

    @Test
    void preSend_withValidToken_setsAuthentication() {

    }
}
