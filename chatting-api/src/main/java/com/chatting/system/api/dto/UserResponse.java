package com.chatting.system.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserResponse {
    private Long id;
    private String username;
    private String token;

    public static UserResponse toUserResponse(Long id, String username, String token) {
        return new UserResponse(id, username, token);
    }
}
