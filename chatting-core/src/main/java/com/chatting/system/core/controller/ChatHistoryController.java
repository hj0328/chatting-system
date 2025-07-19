package com.chatting.system.core.controller;

import com.chatting.system.core.dto.ChatMessageDto;
import com.chatting.system.core.service.ChatHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatHistoryController {
    private final ChatHistoryService chatHistoryService;

    @GetMapping("/{roomId}/messages")
    public ResponseEntity<List<ChatMessageDto>> getRecentRoomMessages(
            @PathVariable String roomId,
            @RequestParam(defaultValue = "0-0") String lastSeenId
    ) {
        List<ChatMessageDto> messages = chatHistoryService.readRecentRoomMessages(roomId, lastSeenId);
        return ResponseEntity.ok(messages);
    }
}
