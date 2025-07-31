package com.chatting.system.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LoginResponse {
    private Long id;
    private String username;

    public static LoginResponse toUserResponse(Long id, String username) {
        return new LoginResponse(id, username);
    }
}
