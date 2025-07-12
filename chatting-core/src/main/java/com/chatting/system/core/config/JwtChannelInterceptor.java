package com.chatting.system.core.config;

import com.common.JwtUserInfo;
import com.common.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String encodedToken = accessor.getFirstNativeHeader("Authorization");

            if (encodedToken != null && encodedToken.startsWith("Bearer ")) {
                String token = encodedToken.substring(7);
                JwtUserInfo jwtUserInfo = jwtUtil.validateTokenAndGetJwtUserInfo(token);

                // Spring Security Authentication 생성
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(jwtUserInfo.getUsername(), null, List.of());

                // SecurityContext에 저장
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // accessor에 user 설정
                accessor.setUser(authentication);
            }
        }

        return message;
    }
}
