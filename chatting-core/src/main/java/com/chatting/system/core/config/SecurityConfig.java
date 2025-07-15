package com.chatting.system.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // WebSocket 사용 시 CSRF 비활성화
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/ws-chat/**", "/health").permitAll() // WebSocket handshake 허용
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form.disable()) // REST API라면 formLogin 비활성화
                .httpBasic(basic -> basic.disable()); // 필요 시 Basic 인증 비활성화

        return http.build();
    }
}
