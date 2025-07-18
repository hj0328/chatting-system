package com.chatting.system.core.subscriber;

import com.chatting.system.core.dto.ChatMessageDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatPubSubListener implements MessageListener {

    private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String json = new String(message.getBody(), StandardCharsets.UTF_8);
            ChatMessageDto dto = objectMapper.readValue(json, ChatMessageDto.class);

            // roomId 기준 topic 브로드캐스트
            messagingTemplate.convertAndSend("/topic/" + dto.getRoomId(), dto);

            log.info("PubSub 수신 → WebSocket 브로드캐스트: {}", dto);
        } catch (Exception e) {
            log.error("PubSub 메시지 처리 실패", e.getStackTrace()[0]);
        }
    }
}
