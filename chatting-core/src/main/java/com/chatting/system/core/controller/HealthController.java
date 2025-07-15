package com.chatting.system.core.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @Value("${SERVER_NAME:default}")
    private String serverName;

    @GetMapping("/health")
    public String health() {
        return "Hello from " + serverName;
    }
}
