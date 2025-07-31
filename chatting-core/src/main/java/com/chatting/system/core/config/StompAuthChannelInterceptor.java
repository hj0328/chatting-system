package com.chatting.system.core.config;

import com.common.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class StompAuthChannelInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            log.info("Connect Interceptor");
            Map<String, Object> sessionAttributes = accessor.getSessionAttributes();

            Principal user = (Principal) sessionAttributes.get("user");
            log.info("Connect Interceptor username:{}", user.getName());
            if (user == null) {
                throw new IllegalArgumentException("WebSocket 인증 실패: 세션에 userId 없음");
            }

             accessor.setUser(user);
        }

        return message;
    }
}

