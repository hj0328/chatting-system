package com.chatting.system.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SignupResponse {
    private Long id;
    private String username;

    public static SignupResponse toUserResponse(Long id, String username) {
        return new SignupResponse(id, username);
    }
}