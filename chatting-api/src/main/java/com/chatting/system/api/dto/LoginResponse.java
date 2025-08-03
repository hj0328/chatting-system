package com.chatting.system.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LoginResponse {
    private Long userId;
    private String username;

    public static LoginResponse toUserResponse(Long userId, String username) {
        return new LoginResponse(userId, username);
    }
}
