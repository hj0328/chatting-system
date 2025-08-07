package com.chatting.system.api.dto;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class LoginResponse {
    private Long userId;
    private String username;
    private String accessToken;

    public LoginResponse(Long userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    public static LoginResponse toUserResponse(Long userId, String username) {
        return new LoginResponse(userId, username);
    }

    public void addAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
