package com.common;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class JwtUserInfo {
    private String username;
    private Long userId;
}
