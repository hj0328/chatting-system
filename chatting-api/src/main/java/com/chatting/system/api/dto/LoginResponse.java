package com.chatting.system.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LoginResponse {
    private Long id;
    private String username;
    private String token;

    public static LoginResponse toUserResponse(Long id, String username, String token) {
        return new LoginResponse(id, username, token);
    }
}
