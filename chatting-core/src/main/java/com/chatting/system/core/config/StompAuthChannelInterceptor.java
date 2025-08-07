package com.chatting.system.core.config;

import com.common.JwtUserInfo;
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

            String tokenHeader = accessor.getFirstNativeHeader("Authorization");

            if (tokenHeader != null && tokenHeader.startsWith("Bearer ")) {
                String token = tokenHeader.substring(7);

                if (jwtUtil.isTokenValid(token, true)) {
                    JwtUserInfo userInfo = jwtUtil.getJwtUserInfo(token, true);
                    log.info("Channel Interceptor usernfo={}", userInfo);
                    accessor.setUser(new StompPrincipal(userInfo.getUsername())); // WebSocket 세션에 Principal 등록
                } else {
                    throw new IllegalArgumentException("Invalid accessToken");
                }
            }


            log.info("Connect Interceptor");
        }

        return message;
    }
}

