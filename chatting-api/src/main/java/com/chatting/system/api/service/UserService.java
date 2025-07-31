package com.chatting.system.api.service;

import com.chatting.system.api.dto.LoginResponse;
import com.chatting.system.api.dto.SignupResponse;
import com.chatting.system.api.entity.User;
import com.chatting.system.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public SignupResponse signup(String username, String password) {
        if (userRepository.findByUsername(username) != null) {
            throw new IllegalArgumentException("이미 존재하는 사용자입니다.");
        }

        User sign = User.sign(username, passwordEncoder.encode(password));
        User saveUser = userRepository.save(sign);

        return SignupResponse.toUserResponse(saveUser.getId(), saveUser.getUsername());
    }

    public LoginResponse login(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {

            return LoginResponse.toUserResponse(user.getId(), user.getUsername());
        }

        throw new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다.");
    }

    public void addRefreshTokenToRedis(Long id, String username, String refreshToken) {
        redisTemplate.opsForValue()
                .set("refresh:" + id + ":" +  username, refreshToken, Duration.ofDays(14));
    }
}
