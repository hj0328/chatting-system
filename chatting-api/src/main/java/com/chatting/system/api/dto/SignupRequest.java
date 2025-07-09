package com.chatting.system.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class SignupRequest {
    @NotBlank(message = "이름은 필수입니다.")
    private String username;
    @NotBlank(message = "비밀번호는 필수입니다. ")
    private String password;
}
