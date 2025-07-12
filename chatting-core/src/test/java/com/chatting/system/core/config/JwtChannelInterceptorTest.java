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
    private JwtChannelInterceptor interceptor;

    @BeforeEach
    void setUp() {
        jwtUtil = mock(JwtUtil.class);
        interceptor = new JwtChannelInterceptor(jwtUtil);
        SecurityContextHolder.clearContext(); // 초기화
    }

    @Test
    void preSend_withValidToken_setsAuthentication() {
        // given
        String token = "testToken";
        String username = "testuser";
        Long userId = 1L;
        JwtUserInfo userInfo = JwtUserInfo.builder()
                .username(username)
                .userId(userId)
                .build();

        when(jwtUtil.validateTokenAndGetJwtUserInfo(token)).thenReturn(userInfo);

        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
        accessor.addNativeHeader("Authorization", "Bearer " + token);
        accessor.setLeaveMutable(true);

        Message<byte[]> message = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());

        // when
        Message<?> result = interceptor.preSend(message, mock(MessageChannel.class));

        // then
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(username, SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        verify(jwtUtil, times(1)).validateTokenAndGetJwtUserInfo(token);
    }
}
