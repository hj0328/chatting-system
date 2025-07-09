package com.chatting.system.api.service;

import com.chatting.system.api.dto.UserResponse;
import com.chatting.system.api.entity.User;
import com.chatting.system.api.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import util.JwtUtil;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public UserResponse signup(String username, String password) {
        if (userRepository.findByUsername(username) != null) {
            throw new IllegalArgumentException("이미 존재하는 사용자입니다.");
        }

        User sign = User.sign(username, passwordEncoder.encode(password));
        User saveUser = userRepository.save(sign);

        String token = jwtUtil.generateToken(saveUser.getUsername(), saveUser.getId());
        return UserResponse.toUserResponse(saveUser.getId(), saveUser.getUsername(), token);
    }

    public UserResponse login(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {

            String token = jwtUtil.generateToken(user.getUsername(), user.getId());
            return UserResponse.toUserResponse(user.getId(), user.getUsername(), token);
        }

        throw new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다.");
    }
}
