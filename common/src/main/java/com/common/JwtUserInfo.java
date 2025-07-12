package com.common;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JwtUserInfo {
    private String username;
    private Long userId;
}
