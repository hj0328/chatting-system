package com.chatting.system.api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@Table(name = "user_table")
@Entity
@Getter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;

    private User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public static User sign(String username, String password) {
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            throw new IllegalArgumentException("값이 비어있을 수 없습니다.");
        }

        return new User(username, password);
    }
}
