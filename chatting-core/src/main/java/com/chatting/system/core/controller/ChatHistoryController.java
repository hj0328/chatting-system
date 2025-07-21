package com.chatting.system.core.controller;

import com.chatting.system.core.dto.ChatMessageDto;
import com.chatting.system.core.service.ChatHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatHistoryController {
    private final ChatHistoryService chatHistoryService;

    @GetMapping("/room/{roomId}/messages")
    public ResponseEntity<List<ChatMessageDto>> getRecentRoomMessages(
            @PathVariable String roomId
    ) {
        log.info("GET Room {}'s message history", roomId);
        List<ChatMessageDto> messages = chatHistoryService.readRecentRoomMessages(roomId);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/dm/{roomId}/messages")
    public ResponseEntity<List<ChatMessageDto>> getRecentDirectMessages(
            @PathVariable String roomId
    ) {
        log.info("GET Direct {}'s message history", roomId);
        List<ChatMessageDto> messages = chatHistoryService.readRecentDirectMessages(roomId);
        return ResponseEntity.ok(messages);
    }
}
